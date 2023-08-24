package io.flamingock.core.stage.executor;

import io.flamingock.core.task.navigation.summary.ProcessSummary;

public class StageExecutionException extends RuntimeException {

    private final ProcessSummary summary;


    public StageExecutionException(ProcessSummary summary) {
        this.summary = summary;
    }

    public StageExecutionException(Throwable throwable, ProcessSummary summary) {
        super(throwable);
        this.summary = summary;
    }

    public ProcessSummary getSummary() {
        return summary;
    }
}
