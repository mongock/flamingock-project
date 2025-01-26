package io.flamingock.common.test.cloud;


import io.flamingock.core.cloud.api.audit.AuditEntryRequest;
import io.flamingock.core.cloud.api.transaction.OngoingStatus;

public class AuditEntryExpectation extends AuditEntryExpectationBase {
    private final AuditEntryRequest.Status state;
    private final OngoingStatus ongoingStatus;


    public AuditEntryExpectation(String taskId,
                                 AuditEntryRequest.Status state,
                                 String className,
                                 String methodName) {
        this(taskId, state, className, methodName, true);
    }

    public AuditEntryExpectation(String taskId,
                                 AuditEntryRequest.Status state,
                                 String className,
                                 String methodName,
                                 boolean transactional) {
        this(taskId, null, state, className, methodName, transactional);
    }

    public AuditEntryExpectation(String taskId,
                                 OngoingStatus ongoingStatus,
                                 AuditEntryRequest.Status state,
                                 String className,
                                 String methodName,
                                 boolean transactional) {
        super(taskId, className, methodName, transactional);
        this.state = state;
        this.ongoingStatus = ongoingStatus;
    }


    public AuditEntryRequest.Status getState() {
        return state;
    }

    public OngoingStatus getOngoingStatus() {
        return ongoingStatus;
    }
}
