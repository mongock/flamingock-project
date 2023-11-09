package io.flamingock.core.task.descriptor;

import java.util.Optional;

public interface TaskDescriptor extends Comparable<TaskDescriptor> {

    String getId();

    boolean isRunAlways();

    boolean isTransactional();

    String getSourceName();

    Optional<String> getOrder();

    default String pretty() {
        return getOrder().isPresent() ? String.format("%s) %s ", getOrder().get(), getId()) : String.format(" %s ", getId());
    }

    default boolean isSortable() {
        return getOrder().isPresent();
    }

    @Override
    default int compareTo(TaskDescriptor other) {
        if (!other.getOrder().isPresent()) {
            return -1;
        } else if (!this.getOrder().isPresent()) {
            return 1;
        } else {
            return this.getOrder().get().compareTo(other.getOrder().get());
        }
    }

}
