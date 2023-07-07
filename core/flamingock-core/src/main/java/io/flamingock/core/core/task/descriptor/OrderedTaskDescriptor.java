package io.flamingock.core.core.task.descriptor;

public interface OrderedTaskDescriptor extends TaskDescriptor<OrderedTaskDescriptor> {

    String getOrder();

    default String pretty() {
        return String.format("%s) %s ", getOrder(), getId());
    }

    default int compareTo(OrderedTaskDescriptor other) {
        return this.getOrder().compareTo(other.getOrder());
    }
}
