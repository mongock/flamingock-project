package io.flamingock.core.core.task.descriptor;

public interface SortedTaskDescriptor extends TaskDescriptor, Comparable<SortedTaskDescriptor> {

    String getOrder();

    default String pretty() {
        return String.format("%s) %s ", getOrder(), getId());
    }

    default int compareTo(SortedTaskDescriptor other) {
        return this.getOrder().compareTo(other.getOrder());
    }
}
