package io.mongock.core.task.descriptor;

import java.util.Comparator;

public interface TaskDescriptor extends Comparable<TaskDescriptor> {

    String getId();

    String getOrder();

    boolean isRunAlways();

    String getClassImplementor();


    default String pretty() {
        return String.format("%s) %s ", getOrder(), getId());
    }

    default int compareTo(TaskDescriptor other) {
        return this.getOrder().compareTo(other.getOrder());
    }
}
