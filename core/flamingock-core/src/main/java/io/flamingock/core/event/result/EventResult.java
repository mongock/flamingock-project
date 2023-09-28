package io.flamingock.core.event.result;

public class EventResult {

  private final boolean success;

  protected EventResult(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
