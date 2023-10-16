package io.flamingock.core.springboot.v3.event;


import io.flamingock.core.event.model.PipelineCompletedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineCompletedEvent extends ApplicationEvent implements PipelineCompletedEvent {

  private final PipelineCompletedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringPipelineCompletedEvent(Object source, PipelineCompletedEvent event) {
    super(source);
    this.event = event;
  }


  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + event +
        "} " + super.toString();
  }


  @Override
  public boolean isSuccess() {
    return event.isSuccess();
  }

  @Override
  public Object getResult() {
    return event.getResult();
  }
}
