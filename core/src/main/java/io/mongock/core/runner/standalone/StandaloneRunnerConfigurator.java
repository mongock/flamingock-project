package io.mongock.core.runner.standalone;



import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.event.MigrationFailureEvent;
import io.mongock.core.event.MigrationStartedEvent;
import io.mongock.core.event.MigrationSuccessEvent;
import io.mongock.core.runner.RunnerConfigurator;

import java.util.function.Consumer;

public interface StandaloneRunnerConfigurator<HOLDER, CONFIG extends AbstractConfiguration>
extends RunnerConfigurator<HOLDER, CONFIG> {

  //TODO javadoc
  HOLDER setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  HOLDER setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  HOLDER setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);
}
