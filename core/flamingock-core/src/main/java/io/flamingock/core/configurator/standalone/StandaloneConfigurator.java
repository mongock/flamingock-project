package io.flamingock.core.configurator.standalone;


import io.flamingock.core.event.model.IPipelineCompletedEvent;
import io.flamingock.core.event.model.IPipelineFailedEvent;
import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.core.event.model.IPipelineStartedEvent;
import io.flamingock.core.event.model.IStageCompletedEvent;
import io.flamingock.core.event.model.IStageFailedEvent;
import io.flamingock.core.event.model.IStageIgnoredEvent;
import io.flamingock.core.event.model.IStageStartedEvent;
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
    HOLDER setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener);

    //TODO javadoc
    HOLDER setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener);

    //TODO javadoc
    HOLDER setPipelineFailureListener(Consumer<IPipelineFailedEvent> listener);

    //TODO javadoc
    HOLDER setStageStartedListener(Consumer<IStageStartedEvent> listener);

    //TODO javadoc
    HOLDER setStageCompletedListener(Consumer<IStageCompletedEvent> listener);

    //TODO javadoc
    HOLDER setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener);

    //TODO javadoc
    HOLDER setStageFailureListener(Consumer<IStageFailedEvent> listener);

    Consumer<IPipelineStartedEvent> getPipelineStartedListener();

    //TODO javadoc
    Consumer<IPipelineCompletedEvent> getPipelineCompletedListener();

    //TODO javadoc
    Consumer<IPipelineIgnoredEvent> getPipelineIgnoredListener();

    //TODO javadoc
    Consumer<IPipelineFailedEvent> getPipelineFailureListener();

    Consumer<IStageStartedEvent> getStageStartedListener();

    //TODO javadoc
    Consumer<IStageCompletedEvent> getStageCompletedListener();

    //TODO javadoc
    Consumer<IStageIgnoredEvent> getStageIgnoredListener();

    //TODO javadoc
    Consumer<IStageFailedEvent> getStageFailureListener();
}
