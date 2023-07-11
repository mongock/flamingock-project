package io.flamingock.core.core.execution.step.complete;

import io.flamingock.core.core.execution.step.AbstractTaskStep;
import io.flamingock.core.core.task.executable.ExecutableTask;

public final class CompletedAlreadyAppliedStep extends AbstractTaskStep {

    public CompletedAlreadyAppliedStep(ExecutableTask executedTask) {
        super(executedTask);
    }

}
