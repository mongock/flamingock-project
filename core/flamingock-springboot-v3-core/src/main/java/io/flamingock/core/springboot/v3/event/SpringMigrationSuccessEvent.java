package io.flamingock.core.springboot.v3.event;


import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.model.Event;
import io.flamingock.core.event.model.CompletedEvent;
import io.flamingock.core.event.model.SuccessEvent;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements ResultEvent {

  private final SuccessEvent event;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, SuccessEvent event) {
    super(source);
    this.event = event;
  }

  @Override
  public Event getSuccessEvent() {
    return event;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + event +
        "} " + super.toString();
  }
}
