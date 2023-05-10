package io.mongock.core.execution.step.execution;

import io.mongock.core.util.Result;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.FailedWithError;

public final class FailedExecutionStep extends ExecutionStep implements FailedWithError {
    private final Throwable throwable;

    public static FailedExecutionStep instance(ExecutableStep initialStep, long executionTimeMillis, Throwable throwable) {
        return new FailedExecutionStep(initialStep.getTask(), executionTimeMillis, throwable);
    }

    private FailedExecutionStep(ExecutableTask task, long executionTimeMillis, Throwable throwable) {
        super(task, false, executionTimeMillis);
        this.throwable = throwable;
    }

    @Override
    public Throwable getError() {
        return throwable;
    }

    @Override
    public AfterExecutionAuditStep applyAuditResult(Result auditResult) {
        return FailedExecutionOrAuditStep.instance(task, auditResult);
    }

}
