package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;


/**
 * Provides abstraction for delegation pattern with EXECUTABLE_TASK.
 *
 * @param <DESCRIPTOR>      Task descriptor
 * @param <EXECUTABLE_TASK> ExutableTask delegate
 */
public abstract class AbstractRollableTask<
        DESCRIPTOR extends TaskDescriptor,
        EXECUTABLE_TASK extends AbstractExecutableTask<DESCRIPTOR>> implements RollableTask {

    protected final EXECUTABLE_TASK baseTask;

    public AbstractRollableTask(EXECUTABLE_TASK baseTask) {
        this.baseTask = baseTask;
    }


    @Override
    public void execute(RuntimeManager runtimeHelper) {
        baseTask.execute(runtimeHelper);
    }

    @Override
    public DESCRIPTOR getDescriptor() {
        return baseTask.getDescriptor();
    }

    @Override
    public boolean isInitialExecutionRequired() {
        return baseTask.isInitialExecutionRequired();
    }

    @Override
    public boolean equals(Object o) {
        return baseTask.equals(o);
    }

    @Override
    public int hashCode() {
        return baseTask.hashCode();
    }

    @Override
    public String toString() {
        return baseTask.toString();
    }
}
