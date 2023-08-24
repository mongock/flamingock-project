package io.flamingock.core.event.result;

public class MigrationIgnoredResult extends MigrationSuccessResult {

  public MigrationIgnoredResult() {
    super(true);
  }

}
