package io.flamingock.core.core.execution.step.execution;

import io.flamingock.core.core.execution.step.ExecutableStep;
import io.flamingock.core.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.core.core.execution.step.complete.CompletedSuccessStep;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.Result;

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
