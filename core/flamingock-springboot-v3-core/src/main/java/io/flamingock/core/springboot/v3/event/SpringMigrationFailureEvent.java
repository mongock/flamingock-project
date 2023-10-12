package io.flamingock.core.springboot.v3.event;

import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.model.FailedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationFailureEvent extends ApplicationEvent implements ResultEvent {
  private final FailedEvent migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationFailureEvent(Object source, Exception ex) {
    super(source);
    migrationResult = new FailedEvent(ex);
  }



  @Override
  public String toString() {
    return "SpringMigrationFailureEvent{" +
        "migrationResult=" + migrationResult +
        ", source=" + source +
        "} " + super.toString();
  }
}
