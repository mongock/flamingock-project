package io.flamingock.oss.core.execution.step.complete;

import io.flamingock.oss.core.execution.step.TaskStep;
import io.flamingock.oss.core.task.executable.ExecutableTask;

public final class CompletedAlreadyAppliedStep extends TaskStep {

    public CompletedAlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
