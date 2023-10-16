package io.flamingock.core.configurator.standalone;


import io.flamingock.core.event.model.PipelineCompletedEvent;
import io.flamingock.core.event.model.PipelineFailedEvent;
import io.flamingock.core.event.model.PipelineIgnoredEvent;
import io.flamingock.core.event.model.PipelineStartedEvent;
import io.flamingock.core.runtime.dependency.DependencyContext;

import java.util.function.Consumer;

public interface StandaloneConfigurator<HOLDER> {

    DependencyContext getDependencyContext();

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by its own type
     *
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a name
     *
     * @param name     name for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(String name, Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type
     *
     * @param type     type for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(Class<?> type, Object instance);

    /**
     * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type or name
     *
     * @param name     name for which it should be searched by
     * @param type     type for which it should be searched by
     * @param instance dependency
     * @return builder for fluent interface
     */
    HOLDER addDependency(String name, Class<?> type, Object instance);

    //TODO javadoc
    HOLDER setMigrationStartedListener(Consumer<PipelineStartedEvent> listener);

    //TODO javadoc
    HOLDER setMigrationSuccessListener(Consumer<PipelineCompletedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineIgnoredListener(Consumer<PipelineIgnoredEvent> listener);

    //TODO javadoc
    HOLDER setMigrationFailureListener(Consumer<PipelineFailedEvent> listener);

    Consumer<PipelineStartedEvent> getMigrationStartedListener();

    //TODO javadoc
    Consumer<PipelineCompletedEvent> getMigrationSuccessListener();

    //TODO javadoc
    Consumer<PipelineIgnoredEvent> getPipelineIgnoredListener();

    //TODO javadoc
    Consumer<PipelineFailedEvent> getMigrationFailureListener();
}
