package com.hist.batch.common.core;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.hist.batch.common.listener.DefaultStepListener;

public abstract class AbstractJobStepFactoryBean implements FactoryBean<Step>, BeanNameAware {
	
	private String beanName;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Override
	public final Class<?> getObjectType() {
		return Step.class;
	}

	@Override
	public final void setBeanName(String name) {
		beanName = name;
	}
	
	protected abstract Step defineStep(StepBuilder stepBuilder);
	
	@Override
	public final Step getObject() throws Exception {
		return defineStep(stepBuilderFactory.get(beanName).listener(new DefaultStepListener<>()));
	}
}
