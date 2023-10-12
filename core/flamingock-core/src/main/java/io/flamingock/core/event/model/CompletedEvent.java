package io.flamingock.core.event.model;

public interface CompletedEvent extends Event {

  @Override
  default boolean isSuccess() {
    return true;
  }

  Object getResult();
}
