package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.model.CompletedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineCompletedEvent extends ApplicationEvent implements CompletedEvent {

  private final CompletedEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringPipelineCompletedEvent(Object source, CompletedEvent event) {
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
