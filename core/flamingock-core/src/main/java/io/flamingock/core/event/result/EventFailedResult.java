package io.flamingock.core.event.result;

public class EventFailedResult extends EventResult {

  private final Exception exception;

  public EventFailedResult(Exception exception) {
    super(false);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
