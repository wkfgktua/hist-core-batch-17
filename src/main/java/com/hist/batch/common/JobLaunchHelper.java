package com.hist.batch.common;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hist.batch.common.cons.BatchConst;
import com.hist.batch.common.log.BatchLog;
import com.hist.batch.common.log.BatchLogFactory;
import com.hist.batch.common.model.BatchLaunchResultModel;

import lombok.extern.slf4j.Slf4j;

/**
*
* @Desc	 :
* @DBAccess :
* @Company  :
* @Project  :
* @Since	:  2023-07-13
* @Author   :  dlehdusmusic@histmate.co.kr
*
*/

@Slf4j
@Component
public class JobLaunchHelper {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private	ApplicationContext springContext;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private SimpleJobLauncher simpleJobLauncher;

	public static final String successDefaultMsg = null;
	public static final String successDefaultCode = "0";
	public static final String knownErrorCode = "1";
	public static final String unKnownErrorCode = "2";
	public static final String asyncStarted = "4";

	public BatchLaunchResultModel run(String jobName, JobParameters jobParams) {
		Job job;

		try {
			job	= springContext.getBean(jobName, Job.class);
		} catch (BeansException e) {
			log.error("Can't run unknown batch job : " + jobName);
			return new BatchLaunchResultModel(unKnownErrorCode, "unknown batch " + jobName);
		}

		log.debug("Start Batch Job : " + jobName);

		try {

			JobExecution je;

			if(jobParams != null && "Y".equals(jobParams.getString("asyncYn"))) {
				je = simpleJobLauncher.run(job, jobParams);
			} else {
				je = jobLauncher.run(job, jobParams);
			}

			try {
				log.debug("End Batch Job : " + jobName);
				return this.makeResult(je);
			} catch (Exception e) {
				log.error("Error Batch Result : " + jobName, e);
				return new BatchLaunchResultModel(je.getJobInstance().getJobName(), je.getId(), unKnownErrorCode, e.getMessage());
			} finally {
				if(jobParams == null || !"Y".equals(jobParams.getString("asyncYn"))) {
					if (BatchLogFactory.isLogExist(je)) {
						BatchLogFactory.clear(je);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error Batch Job : " + jobName, e);
			return new BatchLaunchResultModel(unKnownErrorCode, e.getMessage());
		}
	}

	public BatchLaunchResultModel makeResult(JobExecution je) {
		BatchLaunchResultModel rv = new BatchLaunchResultModel();

		rv.setJobName(je.getJobInstance().getJobName());
		rv.setJobExecutionId(je.getId());
		rv.setStatus(je.getStatus());
		rv.setExitCode(je.getExitStatus().getExitCode());
		rv.setStartTime(je.getStartTime());
		rv.setEndTime(je.getEndTime());

		if (BatchLogFactory.isLogExist(je)) {
			BatchLog batchLog = BatchLogFactory.getLog(je);

			rv.setTotalCnt(batchLog.getTotalCnt());
			rv.setErrorCnt(batchLog.getErrorCnt());

			if (batchLog.getShowResult()) {
				batchLog.add("total:" + batchLog.getTotalCnt() + ",error:" + batchLog.getErrorCnt());
			}

			if (batchLog.hasParams()) {
				try {
					rv.setParam(objectMapper.writeValueAsString(batchLog.getParam()));
				} catch (JsonProcessingException e) {
					log.error("Parsing error JobLaunchHelper BatchLog : {}, jobExecutionId : {}", e.getMessage(), je.getId());
				}
			}

			switch(je.getStatus()) {
				case COMPLETED:
					if (batchLog.isHasError()) {
						rv.setCode(knownErrorCode);
						rv.setMessage(batchLog.toString());
					} else {
						rv.setCode(successDefaultCode);
						rv.setMessage(batchLog.toString());
					}
					break;
				case STOPPED:
					if ("COMPLETED".equals(je.getExitStatus().getExitCode())) {
						if (batchLog.isHasError()) {
							rv.setCode(knownErrorCode);
							rv.setMessage(batchLog.toString());
						} else {
							rv.setCode(successDefaultCode);
							rv.setMessage(batchLog.toString());
						}
					} else {
						rv.setCode(unKnownErrorCode);
						rv.setMessage(batchLog.toString() + BatchConst.BATCH_LOG_SEPERATOR + je.getAllFailureExceptions().toString());
					}
					break;
				case STARTED:
				case STARTING:
					rv.setCode(asyncStarted);
					rv.setMessage("Async Job Starting.");
					break;
				case FAILED:
					rv.setCode(unKnownErrorCode);
					rv.setMessage(batchLog.toString() + BatchConst.BATCH_LOG_SEPERATOR + je.getAllFailureExceptions().toString());
					break;
				default:
					rv.setCode(unKnownErrorCode);
					rv.setMessage(batchLog.toString() + BatchConst.BATCH_LOG_SEPERATOR + je.getAllFailureExceptions().toString());
			}
		} else {
			switch(je.getStatus()) {
				case COMPLETED:
					rv.setCode(successDefaultCode);
					break;
				case STOPPED:
					if ("COMPLETED".equals(je.getExitStatus().getExitCode())) {
						rv.setCode(successDefaultCode);
					} else {
						rv.setCode(unKnownErrorCode);
					}
					break;
				case STARTED:
				case STARTING:
					rv.setCode(asyncStarted);
					rv.setMessage("Async Job Starting.");
					break;
				case FAILED:
					rv.setCode(unKnownErrorCode);
					rv.setMessage(je.getAllFailureExceptions().toString());
					break;
				default:
					rv.setCode(unKnownErrorCode);
					rv.setMessage(je.getAllFailureExceptions().toString());
			}
		}

		return rv;
	}
}
