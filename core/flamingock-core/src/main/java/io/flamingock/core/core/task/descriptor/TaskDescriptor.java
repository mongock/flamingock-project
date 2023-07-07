package io.flamingock.core.core.task.descriptor;

public interface TaskDescriptor<T extends TaskDescriptor<T>> extends Comparable<T> {

    String getId();

    boolean isRunAlways();

    String getClassImplementor();

}
