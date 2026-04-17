package com.hist.batch.common.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hist.batch.common.config.RunningJobFactory;
import com.hist.batch.common.cons.BatchConst;
import com.hist.batch.common.log.BatchLog;
import com.hist.batch.common.log.BatchLogFactory;
import com.hist.batch.common.multiSync.MultiSync;
import com.hist.batch.common.multiSync.MultiSyncFactory;
import com.hist.batch.common.util.StrUtil;
import com.hist.core.util.MDCUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 배치 작업 실행 리스너
 * 배치로그 및 작업종료 callback 등을 처리합니다.
 * @author dlehdusmusic@histmate.co.kr
 *
 */
@Slf4j
@Component
public class DefaultJobExecutionListener implements JobExecutionListener, InitializingBean {

	@Autowired
	private ObjectMapper objectMapper;

	private static final int MAX_EXIT_MESSAGE_LENGTH = 2500;
	private static final int PARAM_OVERHEAD = 11;   				// ,"param":""
	private static final int MESSAGE_OVERHEAD = 13; 				// ,"message":""
	private static final String STATUS_COMPLETED = "COMPLETED";

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		RunningJobFactory.addJob(jobExecution);
		BatchLogFactory.setLogAvailable(jobExecution);

		MDCUtils.set(MDCUtils.SERVICE_NAME, jobExecution.getJobInstance().getJobName());

		if (isMultiSyncEligible(jobExecution.getJobParameters())) {
			MultiSyncFactory.setMultiSync(jobExecution);
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		synchronized (jobExecution) {
			jobExecution.notifyAll();
		}

		RunningJobFactory.removeJob(jobExecution);

		// 1. Exit Message 및 Batch Log 처리
		handleBatchLogAndExitStatus(jobExecution);

		// 2. MultiSync 실패 처리
		handleMultiSyncResult(jobExecution);
	}

	private boolean isMultiSyncEligible(JobParameters params) {
		if (params == null || !"Y".equals(params.getString("multiYn"))) {
			return false;
		}
		return isNumeric(params.getString("exeId"))
			&& isNumeric(params.getString("groupKey"))
			&& isNumeric(params.getString("multiCount"));
	}

	private boolean isNumeric(String str) {
		return str != null && str.matches("\\d+");
	}

	private void handleBatchLogAndExitStatus(JobExecution jobExecution) {
		if (!BatchLogFactory.isLogExist(jobExecution)) {
			return;
		}

		BatchLog batchLog = BatchLogFactory.getLog(jobExecution);

		try {
			String exitMessageJson = buildExitMessageJson(jobExecution, batchLog);
			jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), exitMessageJson));
		} catch (JsonProcessingException e) {
			log.error("Parsing error afterJob BatchLog : {}, jobExecutionId : {}", e.getMessage(), jobExecution.getId());
			jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), e.toString()));
		}

		JobParameters params = jobExecution.getJobParameters();
		if (params != null && "Y".equals(params.getString("asyncYn"))) {
			BatchLogFactory.clear(jobExecution);
		}
	}

	private String buildExitMessageJson(JobExecution jobExecution, BatchLog batchLog) throws JsonProcessingException {
		Map<String, Object> logMap = new HashMap<>();
		logMap.put("totalCnt", String.valueOf(batchLog.getTotalCnt()));
		logMap.put("errorCnt", String.valueOf(batchLog.getErrorCnt()));

		int availableSpace = MAX_EXIT_MESSAGE_LENGTH - objectMapper.writeValueAsString(logMap).length();

		// 1. Param 처리
		availableSpace = applyParamToLogMap(logMap, batchLog, availableSpace);

		// 2. Message (성공/실패 로그) 처리
		applyMessageToLogMap(logMap, jobExecution, batchLog, availableSpace);

		return objectMapper.writeValueAsString(logMap);
	}

	private int applyParamToLogMap(Map<String, Object> logMap, BatchLog batchLog, int availableSpace) throws JsonProcessingException {
		if (!batchLog.hasParams()) {
			return availableSpace;
		}

		String jsonParam = objectMapper.writeValueAsString(batchLog.getParam());
		if (availableSpace > (jsonParam.length() + PARAM_OVERHEAD)) {
			logMap.put("param", jsonParam);
			return availableSpace - (jsonParam.length() + PARAM_OVERHEAD);
		} else {
			String errorMsg = "BatchLog Param is very long. length : " + jsonParam.length();
			logMap.put("param", errorMsg);
			return availableSpace - (errorMsg.length() + PARAM_OVERHEAD);
		}
	}

	private void applyMessageToLogMap(Map<String, Object> logMap, JobExecution jobExecution, BatchLog batchLog, int availableSpace) {
		if (availableSpace <= MESSAGE_OVERHEAD) {
			return; // 메시지를 넣을 공간조차 없음
		}

		availableSpace -= MESSAGE_OVERHEAD;
		boolean isCompleted = STATUS_COMPLETED.equals(jobExecution.getExitStatus().getExitCode());

		if (isCompleted) {
			logMap.put("message", StrUtil.truncateForJsonWithEllipsis(batchLog.toString(), availableSpace));
			return;
		}

		// 실패(Failed) 상태일 경우 Exception 메시지 병합
		String exceptionsStr = jobExecution.getAllFailureExceptions().toString();
		int exceptionJsonLen = StrUtil.getJsonLength(exceptionsStr) + 2;

		if (availableSpace > exceptionJsonLen) {
			availableSpace -= exceptionJsonLen;
			String truncatedLog = StrUtil.truncateForJsonWithEllipsis(batchLog.toString(), availableSpace);
			logMap.put("message", truncatedLog + BatchConst.BATCH_LOG_SEPERATOR + exceptionsStr);
		} else {
			logMap.put("message", StrUtil.truncateForJsonWithEllipsis(exceptionsStr, availableSpace));
		}
	}

	private void handleMultiSyncResult(JobExecution jobExecution) {
		if (MultiSyncFactory.isMultiSyncExist(jobExecution)) {
			MultiSync ms = MultiSyncFactory.getMultiSync(jobExecution);
			boolean isCompleted = STATUS_COMPLETED.equals(jobExecution.getExitStatus().getExitCode());

			if (!ms.getEndYn() && !isCompleted) {
				ms.updateFail();
			}
			MultiSyncFactory.clear(jobExecution);
		}
	}


}
