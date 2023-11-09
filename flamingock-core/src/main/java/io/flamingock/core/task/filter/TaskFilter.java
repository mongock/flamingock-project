package io.flamingock.core.task.filter;

public interface TaskFilter {
    boolean filter(Class<?> filteredElement);
}
