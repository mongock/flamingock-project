package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class SuccessCompleteStep extends AfterExecutionAuditStep {

    public static SuccessCompleteStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new SuccessCompleteStep(executedStep.getTask());
    }

    private SuccessCompleteStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
