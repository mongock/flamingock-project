package io.flamingock.oss.core.event.result;

public class MigrationIgnoredResult extends MigrationSuccessResult {

  public MigrationIgnoredResult() {
    super(true);
  }

}