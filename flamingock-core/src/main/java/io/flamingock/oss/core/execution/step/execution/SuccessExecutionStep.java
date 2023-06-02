package io.flamingock.oss.core.execution.step.execution;

import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.oss.core.execution.step.complete.CompletedSuccessStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.core.execution.step.ExecutableStep;

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
