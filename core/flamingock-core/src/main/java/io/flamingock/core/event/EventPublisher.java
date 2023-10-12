package io.flamingock.core.event;


import io.flamingock.core.event.model.CompletedEvent;
import io.flamingock.core.event.model.IgnoredEvent;
import io.flamingock.core.event.model.SuccessEvent;

import java.util.function.Consumer;

public class EventPublisher {

  private final Runnable pipelineStartedListener;
  private final Consumer<CompletedEvent> pipelineCompletedListener;


  private final Consumer<IgnoredEvent> pipelineIgnoredListener;
  private final Consumer<Exception> pipelineFailedListener;


  public EventPublisher() {
    this(null, null, null, null);
  }

  public EventPublisher(Runnable pipelineStartedListener,
                        Consumer<CompletedEvent> pipelineCompletedListener,
                        Consumer<IgnoredEvent> pipelineIgnoredListener,
                        Consumer<Exception> pipelineFailedListener) {
    this.pipelineStartedListener = pipelineStartedListener;
    this.pipelineCompletedListener = pipelineCompletedListener;
    this.pipelineIgnoredListener = pipelineIgnoredListener;
    this.pipelineFailedListener = pipelineFailedListener;
  }

  public void publishPipelineStarted() {
    if (pipelineStartedListener != null) {
      pipelineStartedListener.run();
    }
  }

  public void publishPipelineSuccessEvent(CompletedEvent event) {
    if (pipelineCompletedListener != null) {
      pipelineCompletedListener.accept(event);
    }
  }

  public void publishPipelineIgnoredEvent(IgnoredEvent event) {
    if (pipelineIgnoredListener != null) {
      pipelineIgnoredListener.accept(event);
    }
  }

  public void publishPipelineFailedEvent(Exception ex) {
    if (pipelineFailedListener != null) {
      pipelineFailedListener.accept(ex);
    }
  }


}
