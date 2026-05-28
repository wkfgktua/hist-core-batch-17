package com.hist.batch.common.builder;

import org.springframework.batch.core.step.builder.AbstractTaskletStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilderHelper;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

public class HistTaskletStepBuilder extends AbstractTaskletStepBuilder<HistTaskletStepBuilder> {

	private Tasklet tasklet;

	public HistTaskletStepBuilder(StepBuilderHelper<?> parent) {
		super(parent);
	}

	public HistTaskletStepBuilder tasklet(Tasklet tasklet) {
		this.tasklet = tasklet;
		return this;
	}

	@Override
	protected Tasklet createTasklet() {
		return tasklet;
	}

	private final DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute() {
		private static final long serialVersionUID = 1L;

		{
			setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
			setIsolationLevel(Isolation.DEFAULT.value());
		}

		@Override
		public boolean rollbackOn(Throwable ex) {
			return true;
		}
	};

	@Override
	public TaskletStep build() {
		TaskletStep step = super.build();

		step.setTransactionAttribute(transactionAttribute);

		return step;
	}
}