package io.flamingock.core.springboot.v2.event;

import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.result.EventFailedResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationFailureEvent extends ApplicationEvent implements ResultEvent {
  private final EventFailedResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationFailureEvent(Object source, Exception ex) {
    super(source);
    migrationResult = new EventFailedResult(ex);
  }


  @Override
  public EventFailedResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationFailureEvent{" +
        "migrationResult=" + migrationResult +
        ", source=" + source +
        "} " + super.toString();
  }
}
