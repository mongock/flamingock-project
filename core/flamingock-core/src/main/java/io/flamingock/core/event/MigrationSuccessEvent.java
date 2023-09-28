package io.flamingock.core.event;


import io.flamingock.core.event.result.EventResult;
import io.flamingock.core.event.result.EventSuccessResult;

public class MigrationSuccessEvent implements ResultEvent {

  private final EventSuccessResult migrationResult;

  public MigrationSuccessEvent(EventSuccessResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public EventResult getMigrationResult() {
    return migrationResult;
  }
}
