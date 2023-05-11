package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class CompletedAlreadyAppliedStep extends TaskStep {

    public CompletedAlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
