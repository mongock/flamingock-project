package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class CompleteSuccessStep extends AfterExecutionAuditStep {

    public static CompleteSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return fromTask(executedStep.getTask());
    }

    public static CompleteSuccessStep fromTask(ExecutableTask executedTask) {
        return new CompleteSuccessStep(executedTask);
    }


    private CompleteSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
