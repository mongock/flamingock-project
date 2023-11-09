package io.flamingock.springboot.v3.event;


import io.flamingock.core.event.model.IPipelineCompletedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineCompletedEvent extends ApplicationEvent implements IPipelineCompletedEvent {

  private final IPipelineCompletedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringPipelineCompletedEvent(Object source, IPipelineCompletedEvent event) {
    super(source);
    this.event = event;
  }

  @Override
  public String toString() {
    return "SpringPipelineCompletedEvent{" +
            "event=" + event +
            ", source=" + source +
            '}';
  }
}
