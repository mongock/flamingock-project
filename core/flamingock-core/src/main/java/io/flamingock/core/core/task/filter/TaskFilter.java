package io.flamingock.core.core.task.filter;

public interface TaskFilter<T> {
    boolean filter(T filteredElement);
}
