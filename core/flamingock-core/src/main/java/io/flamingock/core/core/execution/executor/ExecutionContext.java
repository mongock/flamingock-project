package io.flamingock.core.core.execution.executor;

import java.util.Map;

public class ExecutionContext {
    private final String executionId;

    private final String hostname;

    private final String author;

    private final Map<String, Object> metadata;


    public ExecutionContext(String executionId, String hostname, String author, Map<String, Object> metadata) {
        this.executionId = executionId;
        this.hostname = hostname;
        this.author = author;
        this.metadata = metadata;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getHostname() {
        return hostname;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
