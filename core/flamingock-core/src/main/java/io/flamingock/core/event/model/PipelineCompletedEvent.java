package io.flamingock.core.event.model;

public interface PipelineCompletedEvent extends Event {

  Object getResult();
}
