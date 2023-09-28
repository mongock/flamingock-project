package io.flamingock.core.event.result;

public class EventSuccessResult extends EventResult {

  private final Object result;

  public EventSuccessResult(Object result) {
    super(true);
    this.result = result;
  }

  public Object getResult() {
    return result;
  }
}
