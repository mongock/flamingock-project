package io.flamingock.core.task.navigation.step;

import io.flamingock.core.task.executable.ExecutableTask;

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
