package io.flamingock.core.event.model;

public class FailedEvent implements Event {

  private final Exception exception;

  public FailedEvent(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }
}
