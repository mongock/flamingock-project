package io.flamingock.core.event.model;

public interface StartedEvent extends Event {

  @Override
  default boolean isSuccess() {
    return true;
  }
}
