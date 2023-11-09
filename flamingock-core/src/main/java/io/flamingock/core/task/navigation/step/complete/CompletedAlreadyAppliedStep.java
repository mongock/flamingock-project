package io.flamingock.core.task.navigation.step.complete;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.executable.ExecutableTask;

public final class CompletedAlreadyAppliedStep extends AbstractTaskStep {

    public CompletedAlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
