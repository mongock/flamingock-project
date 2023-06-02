package io.flamingock.oss.core.execution.executor;

import io.flamingock.oss.core.execution.summary.ProcessSummary;

public class ProcessExecutionException extends RuntimeException {

    private final ProcessSummary summary;


    public ProcessExecutionException(ProcessSummary summary) {
        this.summary = summary;
    }

    public ProcessExecutionException(Throwable throwable, ProcessSummary summary) {
        super(throwable);
        this.summary = summary;
    }

    public ProcessSummary getSummary() {
        return summary;
    }
}
