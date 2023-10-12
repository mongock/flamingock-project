package io.flamingock.core.event;

import io.flamingock.core.event.model.FailedEvent;

public class MigrationFailureEvent implements ResultEvent {

  private final FailedEvent migrationResult;

  public MigrationFailureEvent(Exception exception) {

    this.migrationResult = new FailedEvent(exception);
  }

  public Exception getException() {
    return migrationResult.getException();
  }


}
