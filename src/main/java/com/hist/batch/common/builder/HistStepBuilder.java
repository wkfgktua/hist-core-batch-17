package com.hist.batch.common.builder;

import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.builder.FlowStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilderHelper;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.CompletionPolicy;

public class HistStepBuilder extends StepBuilderHelper<HistStepBuilder> {

    public HistStepBuilder(String name) {
        super(name);
    }

    public HistTaskletStepBuilder tasklet(Tasklet tasklet) {
        return new HistTaskletStepBuilder(this).tasklet(tasklet);
    }

    public <I, O> SimpleStepBuilder<I, O> chunk(int chunkSize) {
        return new SimpleStepBuilder<I, O>(this).chunk(chunkSize);
    }

    public <I, O> SimpleStepBuilder<I, O> chunk(CompletionPolicy completionPolicy) {
        return new SimpleStepBuilder<I, O>(this).chunk(completionPolicy);
    }

    public FlowStepBuilder flow(Flow flow) {
        return new FlowStepBuilder(this).flow(flow);
    }
}