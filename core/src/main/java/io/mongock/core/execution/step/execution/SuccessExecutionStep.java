package io.mongock.core.execution.step.execution;

import io.mongock.core.util.Result;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.complete.CompleteSuccessStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.task.executable.ExecutableTask;

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
                ? CompleteSuccessStep.fromSuccessExecution(this)
                : FailedExecutionOrAuditStep.instance(task, auditResult);
    }
}
