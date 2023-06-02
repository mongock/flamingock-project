package io.flamingock.oss.core.task.executable;

import io.flamingock.oss.core.task.descriptor.TaskDescriptor;

import java.util.Objects;

public abstract class AbstractExecutableTask<DESCRIPTOR extends TaskDescriptor> implements ExecutableTask {

    protected final DESCRIPTOR descriptor;

    protected final boolean initialExecutionRequired;

    public AbstractExecutableTask(DESCRIPTOR descriptor, boolean initialExecutionRequired) {
        if (descriptor == null) {
            throw new IllegalArgumentException("Abstract.id cannot be null");
        }
        this.descriptor = descriptor;
        this.initialExecutionRequired = initialExecutionRequired;
    }


    @Override
    public DESCRIPTOR getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean isInitialExecutionRequired() {
        return initialExecutionRequired;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractExecutableTask)) return false;
        AbstractExecutableTask<?> that = (AbstractExecutableTask<?>) o;
        return descriptor.equals(that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    @Override
    public String toString() {
        return "ReflectionExecutableChangeUnit{" +
                ", id='" + descriptor + '\'' +
                ", state=" + initialExecutionRequired +
                "} ";
    }
}
