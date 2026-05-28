package com.hist.batch.common.builder;

import com.hist.batch.common.listener.DefaultJobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistJobBuilderFactory {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private DefaultJobExecutionListener jobExecutionListener;

	public JobBuilder get(String name) {
		JobBuilder builder = new JobBuilder(name).repository(jobRepository).listener(jobExecutionListener);
		return builder;
	}
}