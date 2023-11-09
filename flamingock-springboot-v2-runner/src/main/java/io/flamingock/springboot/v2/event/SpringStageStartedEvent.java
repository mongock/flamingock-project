package io.flamingock.springboot.v2.event;

import io.flamingock.core.event.model.IPipelineStartedEvent;
import io.flamingock.core.event.model.IStageStartedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringStageStartedEvent extends ApplicationEvent implements IPipelineStartedEvent {


  private final IStageStartedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringStageStartedEvent(Object source, IStageStartedEvent event) {
    super(source);
    this.event = event;
  }

  @Override
  public String toString() {
    return "SpringPipelineStartedEvent{" +
            "event=" + event +
            ", source=" + source +
            '}';
  }
}
