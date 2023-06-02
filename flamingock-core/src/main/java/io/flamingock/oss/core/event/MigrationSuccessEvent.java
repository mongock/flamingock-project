package io.flamingock.oss.core.event;


import io.flamingock.oss.core.event.result.MigrationResult;
import io.flamingock.oss.core.event.result.MigrationSuccessResult;

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
