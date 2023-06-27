package io.flamingock.core.core.task.filter;

public interface TaskFilter {
    boolean filter(Class<?> filteredElement);
}
