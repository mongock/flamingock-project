package io.flamingock.core.event.model;

public class StartedEvent implements Event {

  @Override
  public boolean isSuccess() {
    return true;
  }
}
