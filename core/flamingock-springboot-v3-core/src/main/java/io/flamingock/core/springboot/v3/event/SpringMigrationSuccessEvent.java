package io.flamingock.core.springboot.v3.event;


import io.flamingock.core.event.ResultEvent;
import io.flamingock.core.event.result.EventResult;
import io.flamingock.core.event.result.EventSuccessResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements ResultEvent {

  private final EventSuccessResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, EventSuccessResult migrationResult) {
    super(source);
    this.migrationResult = migrationResult;
  }

  @Override
  public EventResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + migrationResult +
        "} " + super.toString();
  }
}
