package com.hist.batch.common.builder;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HistStepBuilderFactory {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private JobRepository jobRepository;

	public HistStepBuilder get(String name) {
		return new HistStepBuilder(name).repository(jobRepository).transactionManager(transactionManager);
	}
}