package io.mongock.core.process.stubs.task;

import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.task.executable.AbstractExecutableTask;
import io.mongock.core.runtime.DefaultRuntimeHelper;

public abstract class TestExecutableTask extends AbstractExecutableTask<TestTaskDescriptor> {

    private boolean executed = false;
    private boolean rollbackExecuted = false;

    TestExecutableTask(String id, AuditEntryStatus initialState) {
        super(null, AuditEntryStatus.isRequiredExecution(initialState));
//        new TestTaskDescriptor(
//                id,
//                rollbackResult != null && rollbackResult != RollbackResult.NOT_PROVIDED,
//                false),
//                initialState)
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isRollbackExecuted() {
        return rollbackExecuted;
    }

    @Override
    public void execute(DefaultRuntimeHelper runtimeHelper) {
        executed = true;
    }


    @Override
    public String getExecutionMethodName() {
        return "testExecutionMethod";
    }

}
