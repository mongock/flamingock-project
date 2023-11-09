package io.flamingock.core.event.model;

public interface IStageCompletedEvent extends Event {

  Object getResult();
}
