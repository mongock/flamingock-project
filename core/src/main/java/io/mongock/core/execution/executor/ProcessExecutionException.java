package io.mongock.core.execution.executor;

import io.mongock.core.execution.summary.ProcessSummary;

public class ProcessExecutionException extends RuntimeException {

    private final ProcessSummary summary;


    public ProcessExecutionException(ProcessSummary summary) {
        this.summary = summary;
    }

    public ProcessSummary getSummary() {
        return summary;
    }
}
