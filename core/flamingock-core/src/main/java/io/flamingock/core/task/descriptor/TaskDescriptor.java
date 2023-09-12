package io.flamingock.core.task.descriptor;

public interface TaskDescriptor extends Comparable<TaskDescriptor>{

    String getId();

    boolean isRunAlways();

    boolean isTransactional();

    String getClassImplementor();

    String getOrder();

    default String pretty() {
        return String.format("%s) %s ", getOrder(), getId());
    }

    @Override
    default int compareTo(TaskDescriptor other) {
        return this.getOrder().compareTo(other.getOrder());
    }

}
