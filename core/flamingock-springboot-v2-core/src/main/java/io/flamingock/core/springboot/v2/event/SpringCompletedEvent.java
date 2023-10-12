package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.model.SuccessEvent;
import org.springframework.context.ApplicationEvent;

public class SpringCompletedEvent extends ApplicationEvent implements ResultEvent {

  private final SuccessEvent successEvent;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringCompletedEvent(Object source, SuccessEvent event) {
    super(source);
    this.successEvent = event;
  }


  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + successEvent +
        "} " + super.toString();
  }
}
