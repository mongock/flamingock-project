package io.flamingock.core.event;


import io.flamingock.core.event.model.Event;
import io.flamingock.core.event.model.SuccessEvent;

public class MigrationSuccessEvent implements ResultEvent {

  private final SuccessEvent event;

  public MigrationSuccessEvent(SuccessEvent event) {
    this.event = event;
  }


  @Override
  public Event getSuccessEvent() {
    return event;
  }
}
