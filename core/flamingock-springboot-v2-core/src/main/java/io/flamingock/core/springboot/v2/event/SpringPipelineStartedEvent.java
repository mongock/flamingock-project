package io.flamingock.core.springboot.v2.event;

import io.flamingock.core.event.model.StartedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineStartedEvent extends ApplicationEvent implements StartedEvent {


  private final StartedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringPipelineStartedEvent(Object source, StartedEvent event) {
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
