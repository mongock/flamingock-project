package io.flamingock.core.task.navigation.step.complete;

import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.execution.SuccessExecutionStep;
import io.flamingock.core.task.executable.ExecutableTask;

public final class CompletedSuccessStep extends AfterExecutionAuditStep {

    public static CompletedSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new CompletedSuccessStep(executedStep.getTask());
    }

    private CompletedSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
