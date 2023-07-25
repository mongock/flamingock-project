package io.flamingock.core.core.task.descriptor;

public interface TaskDescriptor {

    String getId();

    boolean isRunAlways();

    boolean isTransactional();

    String getClassImplementor();

    String pretty();

}
