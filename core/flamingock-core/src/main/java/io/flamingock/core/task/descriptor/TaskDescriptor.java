package io.flamingock.core.task.descriptor;

public interface TaskDescriptor {

    String getId();

    boolean isRunAlways();

    boolean isTransactional();

    String getClassImplementor();

    String pretty();

}
