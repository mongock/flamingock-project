package io.flamingock.core.pipeline.stage.execution;

import io.flamingock.core.task.navigation.summary.StageSummary;

public class StageExecutionException extends RuntimeException {

    private final StageSummary summary;


    public StageExecutionException(StageSummary summary) {
        this.summary = summary;
    }

    public StageExecutionException(Throwable throwable, StageSummary summary) {
        super(throwable);
        this.summary = summary;
    }

    public StageSummary getSummary() {
        return summary;
    }
}
