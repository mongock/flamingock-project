package io.mongock.core.runner.standalone;



import io.mongock.core.event.MigrationFailureEvent;
import io.mongock.core.event.MigrationStartedEvent;
import io.mongock.core.event.MigrationSuccessEvent;
import io.mongock.core.runtime.dependency.Dependency;

import java.util.function.Consumer;

public interface StandaloneBuilder<HOLDER> {


  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by its own type
   *
   * @param instance dependency
   * @return builder for fluent interface
   */
  default HOLDER addDependency(Object instance) {
    return addDependency(Dependency.DEFAULT_NAME, instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a name
   *
   * @param name     name for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default HOLDER addDependency(String name, Object instance) {
    return addDependency(name, instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type
   *
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default HOLDER addDependency(Class<?> type, Object instance) {
    return addDependency(Dependency.DEFAULT_NAME, type, instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type or name
   *
   * @param name     name for which it should be searched by
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  HOLDER addDependency(String name, Class<?> type, Object instance);

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////

  //TODO javadoc
  HOLDER setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  HOLDER setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  HOLDER setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);
}
