package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.task.executable.ExecutableTask;

public final class AlreadyAppliedStep extends TaskStep {

    public AlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
