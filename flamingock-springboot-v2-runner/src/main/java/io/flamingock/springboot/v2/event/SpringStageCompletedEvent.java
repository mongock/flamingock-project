package io.flamingock.springboot.v2.event;


import io.flamingock.core.event.model.IPipelineCompletedEvent;
import io.flamingock.core.event.model.IStageCompletedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringStageCompletedEvent extends ApplicationEvent implements IPipelineCompletedEvent {

  private final IStageCompletedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringStageCompletedEvent(Object source, IStageCompletedEvent event) {
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
