package io.flamingock.oss.core.execution.step.complete;

import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.execution.step.execution.SuccessExecutionStep;

public final class CompletedSuccessStep extends AfterExecutionAuditStep {

    public static CompletedSuccessStep fromSuccessExecution(SuccessExecutionStep executedStep) {
        return new CompletedSuccessStep(executedStep.getTask());
    }

    private CompletedSuccessStep(ExecutableTask executedTask) {
        super(executedTask, true);
    }

}
