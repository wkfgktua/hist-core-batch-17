package com.hist.batch.common.config;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ShutdownJobListener implements ApplicationListener<ContextClosedEvent> {

	@Value("${histcore.awaitShutdown:-1}")
	private int awaitShutdown;

	@Autowired
    private JobRepository jobRepository;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		for (JobExecution je : RunningJobFactory.getRunningJob()) {
			if (je.isRunning() && je.getStatus() == BatchStatus.STARTED) {
				je.setStatus(BatchStatus.STOPPING);
				je.setExitStatus(new ExitStatus("WAS_SHUTDOWN", "Job stopped due to WAS shutdown after awaitShutdown :" + awaitShutdown));
				je.setEndTime(new Date());
	            jobRepository.update(je);
			}
		}
	}
}
