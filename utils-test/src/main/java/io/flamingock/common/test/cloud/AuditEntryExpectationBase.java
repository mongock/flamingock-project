package io.flamingock.common.test.cloud;


import io.flamingock.core.cloud.api.audit.AuditEntryRequest;
import io.flamingock.core.cloud.api.transaction.OngoingStatus;

import java.beans.Transient;

public class AuditEntryExpectationBase {
    private final String taskId;
    private final String className;
    private final String methodName;

    private final boolean transactional;



    public AuditEntryExpectationBase(String taskId,
                                     String className,
                                     String methodName,
                                     boolean transactional) {
        this.taskId = taskId;
        this.className = className;
        this.methodName = methodName;
        this.transactional = transactional;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Transient
    public boolean isTransactional() {
        return transactional;
    }

    public AuditEntryExpectation withState(OngoingStatus ongoingStatus, AuditEntryRequest.Status state) {
        return new AuditEntryExpectation(taskId, state, className, methodName, transactional);
    }

}
