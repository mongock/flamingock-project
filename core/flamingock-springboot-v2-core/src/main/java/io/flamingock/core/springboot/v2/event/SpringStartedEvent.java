package io.flamingock.core.springboot.v2.event;

import org.springframework.context.ApplicationEvent;

public class SpringStartedEvent extends ApplicationEvent {


  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringStartedEvent(Object source) {
    super(source);
  }

  @Override
  public String toString() {
    return "SpringMigrationStartedEvent{" +
        "source=" + source +
        "} " + super.toString();
  }
}
