package io.flamingock.core.core.configurator.standalone;



import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.runtime.dependency.Dependency;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

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
  HOLDER setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  HOLDER setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  HOLDER setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);

  Consumer<MigrationStartedEvent> getMigrationStartedListener();

  //TODO javadoc
  Consumer<MigrationSuccessEvent> getMigrationSuccessListener();

  //TODO javadoc
  Consumer<MigrationFailureEvent> getMigrationFailureListener();
}
