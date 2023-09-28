package io.flamingock.core.event;

import io.flamingock.core.event.result.EventFailedResult;

public class MigrationFailureEvent implements ResultEvent {

  private final EventFailedResult migrationResult;

  public MigrationFailureEvent(Exception exception) {

    this.migrationResult = new EventFailedResult(exception);
  }

  public Exception getException() {
    return migrationResult.getException();
  }

  @Override
  public EventFailedResult getMigrationResult() {
    return migrationResult;
  }
}
