package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class CompleteSuccessStep extends AfterExecutionAuditStep {

    public static CompleteSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new CompleteSuccessStep(executedStep.getTask());
    }

    private CompleteSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
