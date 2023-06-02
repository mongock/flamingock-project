package io.flamingock.oss.core.execution.step.execution;

import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.FailedWithError;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.core.execution.step.ExecutableStep;

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
