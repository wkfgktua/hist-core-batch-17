package com.hist.batch.common.core;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

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
		//return defineStep(stepBuilderFactory.get(beanName).listener(new DefaultStepListener<>()));

		Step step = defineStep(stepBuilderFactory.get(beanName).listener(new DefaultStepListener<>()));

		if (step instanceof TaskletStep) {
			TaskletStep taskletStep = (TaskletStep) step;

			DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
			attribute.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
			attribute.setIsolationLevel(Isolation.DEFAULT.value());

			taskletStep.setTransactionAttribute(attribute);
		}

		return step;
	}
}
