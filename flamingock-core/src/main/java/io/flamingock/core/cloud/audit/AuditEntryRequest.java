package io.flamingock.core.cloud.audit;

import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.engine.audit.writer.AuditEntryStatus;

public class AuditEntryRequest {


    private final String executionPlanId;

    private final String stageId;

    private final String taskId;

    private final String author;

    private final long createdAt;

    private final AuditEntryStatus state;

    private final String className;

    private final String methodName;

    private final Object metadata;

    private final long executionMillis;

    private final String executionHostname;

    private final String errorTrace;

    private final AuditEntry.ExecutionType type;

    protected Boolean systemChange;

    public AuditEntryRequest(String executionPlanId,
                             String stageId,
                             String taskId,
                             String author,
                             long createdAt,
                             AuditEntryStatus state,
                             AuditEntry.ExecutionType type,
                             String className,
                             String methodName,
                             long executionMillis,
                             String executionHostname,
                             Object metadata,
                             boolean systemChange,
                             String errorTrace) {
        this.executionPlanId = executionPlanId;
        this.stageId = stageId;
        this.taskId = taskId;
        this.author = author;
        this.createdAt = createdAt;
        this.state = state;
        this.className = className;
        this.methodName = methodName;
        this.metadata = metadata;
        this.executionMillis = executionMillis;
        this.executionHostname = executionHostname;
        this.errorTrace = errorTrace;
        this.type = type;

        this.systemChange = systemChange;
    }


    public String getExecutionPlanId() {
        return executionPlanId;
    }

    public String getStageId() {
        return stageId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getAuthor() {
        return author;
    }

    public long getCreatedAt() {
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

    public Boolean getSystemChange() {
        return systemChange;
    }

    public AuditEntry.ExecutionType getType() {
        return type;
    }


}



