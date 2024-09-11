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
import com.fasterxml.uuid.Generators;
import com.hist.batch.common.config.RunningJobFactory;
import com.hist.batch.common.log.BatchLog;
import com.hist.batch.common.log.BatchLogFactory;
import com.hist.batch.common.multiSync.MultiSync;
import com.hist.batch.common.multiSync.MultiSyncFactory;
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

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		RunningJobFactory.addJob(jobExecution);
		BatchLogFactory.setLogAvailable(jobExecution);

		String traceId = MDCUtils.get(MDCUtils.TRACE_ID);
		if (traceId == null || "".equals(traceId)) {
			traceId = Generators.timeBasedGenerator().generate().toString();
		}
		MDCUtils.set(MDCUtils.TRACE_ID, traceId);

		JobParameters jobParam = jobExecution.getJobParameters();
		if(jobParam != null && "Y".equals(jobParam.getString("multiYn")) && jobParam.getString("exeId") != null && jobParam.getString("exeId").matches("\\d+") && jobParam.getString("multiCount") != null && jobParam.getString("multiCount").matches("\\d+")) {
			MultiSyncFactory.setMultiSync(jobExecution);
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		synchronized (jobExecution) {
			jobExecution.notifyAll();
		}

		RunningJobFactory.removeJob(jobExecution);

		if (BatchLogFactory.isLogExist(jobExecution)) {
			BatchLog batchLog = BatchLogFactory.getLog(jobExecution);

			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("totalCnt", String.valueOf(batchLog.getTotalCnt()));
			logMap.put("errorCnt", String.valueOf(batchLog.getErrorCnt()));
			logMap.put("message", batchLog.toString());
			if (batchLog.hasParams()) {
				try {
					logMap.put("param", (objectMapper.writeValueAsString(batchLog.getParam())));
				} catch (JsonProcessingException e) {
					log.error("Parsing error afterJob BatchLog : {}, jobExecutionId : {}", e.getMessage(), jobExecution.getId());
				}
			}

			if("COMPLETED".equals(jobExecution.getExitStatus().getExitCode())) {
				try {
					jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), objectMapper.writeValueAsString(logMap)));
				} catch (JsonProcessingException e) {
					log.error("Parsing error afterJob BatchLog : {}, jobExecutionId : {}", e.getMessage(), jobExecution.getId());
				}
			}

			if(jobExecution != null && jobExecution.getJobParameters() != null && "Y".equals(jobExecution.getJobParameters().getString("asyncYn"))) {
				BatchLogFactory.clear(jobExecution);
			}
		}

		if(MultiSyncFactory.isMultiSyncExist(jobExecution)) {
			MultiSync ms = MultiSyncFactory.getMultiSync(jobExecution);
			if(!ms.getEndYn() && !"COMPLETED".equals(jobExecution.getExitStatus().getExitCode())) {
				ms.updateFail();
			}
			MultiSyncFactory.clear(jobExecution);
		}
	}
}
