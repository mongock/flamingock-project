package io.flamingock.oss.core.event;

import io.flamingock.oss.core.event.result.MigrationFailedResult;

public class MigrationFailureEvent implements MongockResultEvent {

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
