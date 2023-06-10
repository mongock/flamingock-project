package io.flamingock.core.core.event;

import io.flamingock.core.core.event.result.MigrationFailedResult;

public class MigrationFailureEvent implements ResultEvent {

  private final MigrationFailedResult migrationResult;

  public MigrationFailureEvent(Exception exception) {

    this.migrationResult = new MigrationFailedResult(exception);
  }

  public Exception getException() {
    return migrationResult.getException();
  }

  @Override
  public MigrationFailedResult getMigrationResult() {
    return migrationResult;
  }
}
