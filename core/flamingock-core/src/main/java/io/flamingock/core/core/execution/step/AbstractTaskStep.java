package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.task.executable.OrderedExecutableTask;

public abstract class AbstractTaskStep implements TaskStep {

    protected final OrderedExecutableTask task;

    protected AbstractTaskStep(OrderedExecutableTask task) {
        this.task = task;
    }

    @Override
    public OrderedExecutableTask getTask() {
        return task;
    }

}
