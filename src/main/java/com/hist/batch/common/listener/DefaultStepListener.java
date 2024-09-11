package com.hist.batch.common.listener;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;

import lombok.extern.slf4j.Slf4j;

/**
*
* @Desc     :
* @DBAccess :
* @Company  :
* @Project  :
* @Since    :
* @Author   :
*
*/
@Slf4j
public class DefaultStepListener<T, S> extends StepListenerSupport<T, S> {

	private StepExecution stepExecution;

	public void beforeStep(StepExecution stepExecution) {
		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Step started : ")
			  .append("JobName:").append(stepExecution.getJobExecution().getJobInstance().getJobName())
			  .append(",StepName:").append(stepExecution.getStepName())
			  .append(",JobParameters:").append(stepExecution.getJobParameters());
			log.debug(sb.toString());
		}
		this.stepExecution = stepExecution;
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Step completed : ")
			  .append("JobName:").append(stepExecution.getJobExecution().getJobInstance().getJobName())
			  .append(",StepSummary:").append(stepExecution.getSummary());
			log.debug(sb.toString());
		}
		return stepExecution.getExitStatus();
	}

	public void afterWrite(List<? extends S> items) {
		if ((log.isDebugEnabled()) && (items != null)) {
			StringBuffer sb = new StringBuffer();
			sb.append("after write items : ")
			  .append("JobName:").append(this.stepExecution.getJobExecution().getJobInstance().getJobName())
			  .append(",StepName:").append(this.stepExecution.getStepName())
			  .append(",written item count : ").append(items.size());
			log.debug(sb.toString());
		}
	}
}

