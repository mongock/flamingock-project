package io.flamingock.core.configurator.standalone;


import io.flamingock.core.event.model.CompletedEvent;
import io.flamingock.core.event.model.FailedEvent;
import io.flamingock.core.event.model.IgnoredEvent;
import io.flamingock.core.event.model.StartedEvent;
import io.flamingock.core.event.model.SuccessEvent;
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
    HOLDER setMigrationStartedListener(Consumer<StartedEvent> listener);

    //TODO javadoc
    HOLDER setMigrationSuccessListener(Consumer<CompletedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineIgnoredListener(Consumer<IgnoredEvent> listener);

    //TODO javadoc
    HOLDER setMigrationFailureListener(Consumer<FailedEvent> listener);

    Consumer<StartedEvent> getMigrationStartedListener();

    //TODO javadoc
    Consumer<CompletedEvent> getMigrationSuccessListener();

    //TODO javadoc
    Consumer<IgnoredEvent> getPipelineIgnoredListener();

    //TODO javadoc
    Consumer<FailedEvent> getMigrationFailureListener();
}
