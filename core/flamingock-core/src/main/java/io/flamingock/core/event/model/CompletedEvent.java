package io.flamingock.core.event.model;

public class CompletedEvent implements SuccessEvent {

  private final Object result;

  public CompletedEvent(Object result) {
    this.result = result;
  }

  public Object getResult() {
    return result;
  }
}
