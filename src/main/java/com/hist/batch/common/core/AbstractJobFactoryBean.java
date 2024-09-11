package com.hist.batch.common.core;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.hist.batch.common.listener.DefaultJobExecutionListener;

public abstract class AbstractJobFactoryBean implements FactoryBean<Job>, BeanNameAware {

	private String beanName;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private DefaultJobExecutionListener jobExecutionListener;
	
	@Override
	public final Class<?> getObjectType() {
		return Job.class;
	}

	@Override
	public final void setBeanName(String name) {
		this.beanName = name;
	}
	
	protected abstract Job defineJob(JobBuilder jobBuilder);
	
	@Override
	public final Job getObject() throws Exception {
		return defineJob(jobBuilderFactory.get(beanName).listener(jobExecutionListener));
	}
}
