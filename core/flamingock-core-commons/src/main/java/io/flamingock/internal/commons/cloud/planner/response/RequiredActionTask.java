package io.flamingock.internal.commons.cloud.planner.response;


//TODO values should be changed to
// EXECUTION_REQUIRED
// NO_EXECUTION_REQUIRED
public enum RequiredActionTask {
    PENDING_EXECUTION, ALREADY_EXECUTED;


    public boolean requiresExecution() {
        return this == PENDING_EXECUTION;
    }
}
