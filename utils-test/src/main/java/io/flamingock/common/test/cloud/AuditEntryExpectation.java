package io.flamingock.common.test.cloud;


import io.flamingock.core.cloud.api.audit.AuditEntryRequestStatus;

public class AuditEntryExpectation {
    private final String taskId;
    private final AuditEntryRequestStatus state;
    private final String className;
    private final String methodName;


    public AuditEntryExpectation(String taskId, AuditEntryRequestStatus state, String className, String methodName) {
        this.taskId = taskId;
        this.state = state;
        this.className = className;
        this.methodName = methodName;
    }

    public String getTaskId() {
        return taskId;
    }

    public AuditEntryRequestStatus getState() {
        return state;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
