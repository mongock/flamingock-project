package io.flamingock.core.core.execution.step.complete;

import io.flamingock.core.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.core.execution.step.execution.SuccessExecutionStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

public final class CompletedSuccessStep extends AfterExecutionAuditStep {

    public static CompletedSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new CompletedSuccessStep(executedStep.getTask());
    }

    private CompletedSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
