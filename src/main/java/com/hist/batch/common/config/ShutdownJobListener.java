package com.hist.batch.common.config;

import java.time.Duration;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShutdownJobListener implements ApplicationListener<ContextClosedEvent> {

	@Value("${spring.lifecycle.timeout-per-shutdown-phase:0}")
    private Duration timeoutPerShutdownPhase;

	@Autowired
    private JobRepository jobRepository;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		log.error("Graceful Shutdown RunningJobFactory count :" + RunningJobFactory.getSize());
		log.error("Graceful Shutdown RunningJobFactory list :" + RunningJobFactory.getRunningJob());

		for (JobExecution je : RunningJobFactory.getRunningJob()) {
			if (je == null) {
				log.error("Graceful Shutdown je is null !!");
			} else {
				log.error("Graceful Shutdown Job Status update id : " + je.getId() + ", je.isRunning() : " + je.isRunning() + ", je.getStatus() : " + je.getStatus());
				if (je.isRunning() && je.getStatus() == BatchStatus.STARTED) {
					je.setStatus(BatchStatus.STOPPING);
					je.setExitStatus(new ExitStatus("WAS_SHUTDOWN", "Job stopped due to WAS shutdown after await :" + timeoutPerShutdownPhase.toMillis()));
					je.setEndTime(new Date());
		            jobRepository.update(je);
				}
			}
		}
	}
}
