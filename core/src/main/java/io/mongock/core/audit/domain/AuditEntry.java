package io.mongock.core.audit.domain;

import io.mongock.core.util.ThrowableUtil;

import java.time.LocalDateTime;

public class AuditEntry {

    private final String executionId;

    private final String changeId;

    private final String author;

    private final LocalDateTime createdAt;

    private final AuditEntryStatus state;

    private final String className;

    private final String methodName;


    private final Object metadata;

    private final long executionMillis;

    private final String executionHostname;

    private final String errorTrace;


    public static AuditEntry instance(String executionId,
                                      String changeId,
                                      String author,
                                      LocalDateTime createdAt,
                                      AuditEntryStatus state,
                                      String className,
                                      String methodName,
                                      long executionMillis,
                                      String executionHostname,
                                      Object metadata) {
        return new AuditEntry(executionId,
                changeId,
                author,
                createdAt,
                state,
                className,
                methodName,
                executionMillis,
                executionHostname,
                metadata,
                null);
    }

    public static AuditEntry withError(String executionId,
                                       String changeId,
                                       String author,
                                       LocalDateTime createdAt,
                                       AuditEntryStatus state,
                                       String className,
                                       String methodName,
                                       long executionMillis,
                                       String executionHostname,
                                       Object metadata,
                                       Throwable errorTrace) {
        return new AuditEntry(executionId,
                changeId,
                author,
                createdAt,
                state,
                className,
                methodName,
                executionMillis,
                executionHostname,
                metadata,
                ThrowableUtil.serialize(errorTrace));
    }

    protected AuditEntry(String executionId,
                         String changeId,
                         String author,
                         LocalDateTime createdAt,
                         AuditEntryStatus state,
                         String className,
                         String methodName,
                         long executionMillis,
                         String executionHostname,
                         Object metadata,
                         String errorTrace) {
        this.executionId = executionId;
        this.changeId = changeId;
        this.author = author;
        this.createdAt = createdAt;
        this.state = state;
        this.className = className;
        this.methodName = methodName;
        this.metadata = metadata;
        this.executionMillis = executionMillis;
        this.executionHostname = executionHostname;
        this.errorTrace = errorTrace;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getChangeId() {
        return changeId;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AuditEntryStatus getState() {
        return state;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getMetadata() {
        return metadata;
    }

    public long getExecutionMillis() {
        return executionMillis;
    }

    public String getExecutionHostname() {
        return executionHostname;
    }

    public String getErrorTrace() {
        return errorTrace;
    }

}
