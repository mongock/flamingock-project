package io.flamingock.core.task.navigation.step.execution;

import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.core.task.navigation.step.complete.CompletedSuccessStep;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.Result;

public final class SuccessExecutionStep extends ExecutionStep {
    public static SuccessExecutionStep instance(ExecutableStep initialStep, long executionTimeMillis) {
        return new SuccessExecutionStep(initialStep.getTask(), executionTimeMillis);
    }

    private SuccessExecutionStep(ExecutableTask task, long executionTimeMillis) {
        super(task, true, executionTimeMillis);
    }

    @Override
    public AfterExecutionAuditStep applyAuditResult(Result auditResult) {
        return auditResult.isOk()
                ? CompletedSuccessStep.fromSuccessExecution(this)
                : FailedExecutionOrAuditStep.instance(task, auditResult);
    }
}
