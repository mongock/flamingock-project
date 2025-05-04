package io.flamingock.core.configurator;

import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.event.SimpleEventPublisher;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.task.filter.TaskFilter;

import java.util.Collections;
import java.util.List;

public interface BuilderPlugin {

    default EventPublisher getEventPublisher() {
        return new SimpleEventPublisher();
    }

    default List<TaskFilter> getTaskFilters() {
        return Collections.emptyList();
    }

    void setFrameworkComponents(DependencyContext dependencyContext);
}
