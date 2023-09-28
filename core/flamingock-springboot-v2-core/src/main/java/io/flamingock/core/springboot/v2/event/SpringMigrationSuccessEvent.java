package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.model.Event;
import io.flamingock.core.event.model.SuccessEvent;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements ResultEvent {

  private final SuccessEvent successEvent;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, SuccessEvent event) {
    super(source);
    this.successEvent = event;
  }

  @Override
  public Event getSuccessEvent() {
    return successEvent;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + successEvent +
        "} " + super.toString();
  }
}
