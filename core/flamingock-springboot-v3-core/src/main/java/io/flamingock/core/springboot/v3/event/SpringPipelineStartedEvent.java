package io.flamingock.core.springboot.v3.event;

import io.flamingock.core.event.model.PipelineStartedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineStartedEvent extends ApplicationEvent implements PipelineStartedEvent {


  private final PipelineStartedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringPipelineStartedEvent(Object source, PipelineStartedEvent event) {
    super(source);
    this.event = event;
  }

  @Override
  public String toString() {
    return "SpringMigrationStartedEvent{" +
        "source=" + source +
        "} " + super.toString();
  }
}
