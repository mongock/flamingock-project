package io.flamingock.core.task.navigation.step.execution;

import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.FailedWithErrorStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.Result;

public final class FailedExecutionStep extends ExecutionStep implements FailedWithErrorStep {
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
