package io.flamingock.core.event;


import io.flamingock.core.event.result.EventSuccessResult;

import java.util.function.Consumer;

public class EventPublisher {

  private final Runnable flamingockStartedListener;
  private final Consumer<EventSuccessResult> flamingockSuccessListener;
  private final Consumer<Exception> flamingockFailedListener;


  public EventPublisher() {
    this(null, null, null);
  }

  public EventPublisher(Runnable flamingockStartedListener,
                        Consumer<EventSuccessResult> flamingockSuccessListener,
                        Consumer<Exception> flamingockFailedListener) {
    this.flamingockSuccessListener = flamingockSuccessListener;
    this.flamingockFailedListener = flamingockFailedListener;
    this.flamingockStartedListener = flamingockStartedListener;
  }

  public void publishFlamingockStarted() {
    if (flamingockStartedListener != null) {
      flamingockStartedListener.run();
    }
  }

  public void publishFlamingockSuccessEvent(EventSuccessResult migrationResult) {
    if (flamingockSuccessListener != null) {
      flamingockSuccessListener.accept(migrationResult);
    }
  }

  public void publishFlamingockFailedEvent(Exception ex) {
    if (flamingockFailedListener != null) {
      flamingockFailedListener.accept(ex);
    }
  }

  public Runnable getFlamingockStartedListener() {
    return flamingockStartedListener;
  }

  public Consumer<EventSuccessResult> getFlamingockSuccessListener() {
    return flamingockSuccessListener;
  }

  public Consumer<Exception> getFlamingockFailedListener() {
    return flamingockFailedListener;
  }
}
