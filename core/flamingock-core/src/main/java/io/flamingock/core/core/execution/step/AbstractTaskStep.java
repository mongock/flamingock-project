package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;

public abstract class AbstractTaskStep implements TaskStep {

    protected final ExecutableTask task;

    protected AbstractTaskStep(ExecutableTask task) {
        this.task = task;
    }

    @Override
    public ExecutableTask getTask() {
        return task;
    }

}
