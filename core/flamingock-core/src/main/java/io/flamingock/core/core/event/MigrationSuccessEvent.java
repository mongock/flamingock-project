package io.flamingock.core.core.event;


import io.flamingock.core.core.event.result.MigrationResult;
import io.flamingock.core.core.event.result.MigrationSuccessResult;

public class MigrationSuccessEvent implements ResultEvent {

  private final MigrationSuccessResult migrationResult;

  public MigrationSuccessEvent(MigrationSuccessResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}