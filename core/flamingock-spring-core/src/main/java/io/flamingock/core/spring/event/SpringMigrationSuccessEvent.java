package io.flamingock.core.spring.event;


import io.flamingock.core.core.event.result.MigrationResult;
import io.flamingock.core.core.event.result.MigrationSuccessResult;
import io.flamingock.core.core.event.ResultEvent;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements ResultEvent {

  private final MigrationSuccessResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, MigrationSuccessResult migrationResult) {
    super(source);
    this.migrationResult = migrationResult;
  }

  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + migrationResult +
        "} " + super.toString();
  }
}
