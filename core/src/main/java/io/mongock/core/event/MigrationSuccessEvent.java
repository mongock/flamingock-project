package io.mongock.core.event;


import io.mongock.core.event.result.MigrationResult;
import io.mongock.core.event.result.MigrationSuccessResult;

public class MigrationSuccessEvent implements MongockResultEvent {

  private final MigrationSuccessResult migrationResult;

  public MigrationSuccessEvent(MigrationSuccessResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
