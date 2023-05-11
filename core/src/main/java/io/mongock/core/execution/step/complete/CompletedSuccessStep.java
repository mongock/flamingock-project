package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class CompletedSuccessStep extends AfterExecutionAuditStep {

    public static CompletedSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new CompletedSuccessStep(executedStep.getTask());
    }

    private CompletedSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
