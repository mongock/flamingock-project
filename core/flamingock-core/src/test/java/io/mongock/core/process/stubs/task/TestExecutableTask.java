package io.mongock.core.process.stubs.task;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.AbstractExecutableTask;

public abstract class TestExecutableTask extends AbstractExecutableTask<TestTaskDescriptor> {

    private boolean executed = false;
    private final boolean rollbackExecuted = false;

    TestExecutableTask(String id, AuditEntryStatus initialState) {
        super(null, AuditEntryStatus.isRequiredExecution(initialState));
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isRollbackExecuted() {
        return rollbackExecuted;
    }

    @Override
    public void execute(RuntimeManager runtimeHelper) {
        executed = true;
    }


    @Override
    public String getExecutionMethodName() {
        return "testExecutionMethod";
    }

}
