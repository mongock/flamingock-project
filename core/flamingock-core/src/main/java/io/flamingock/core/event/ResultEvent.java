package io.flamingock.core.event;


import io.flamingock.core.event.result.EventResult;

public interface ResultEvent {

  EventResult getMigrationResult();
}
