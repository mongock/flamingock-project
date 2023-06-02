package io.flamingock.core.core.execution.step.complete;

import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

public final class CompletedAlreadyAppliedStep extends TaskStep {

    public CompletedAlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
