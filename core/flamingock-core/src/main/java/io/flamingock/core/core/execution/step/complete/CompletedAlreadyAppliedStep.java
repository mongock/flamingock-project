package io.flamingock.core.core.execution.step.complete;

import io.flamingock.core.core.execution.step.AbstractTaskStep;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;

public final class CompletedAlreadyAppliedStep extends AbstractTaskStep {

    public CompletedAlreadyAppliedStep(OrderedExecutableTask executedTask) {
        super(executedTask);
    }

}
