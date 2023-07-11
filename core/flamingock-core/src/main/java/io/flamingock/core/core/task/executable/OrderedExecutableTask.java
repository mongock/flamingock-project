package io.flamingock.core.core.task.executable;

public interface OrderedExecutableTask extends ExecutableTask, Comparable<OrderedExecutableTask> {
    String getOrder();

    @Override
    default int compareTo(OrderedExecutableTask other) {
        return this.getOrder().compareTo(other.getOrder());
    }

}