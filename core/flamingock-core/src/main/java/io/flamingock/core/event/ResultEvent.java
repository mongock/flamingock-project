package io.flamingock.core.event;


import io.flamingock.core.event.result.MigrationResult;

public interface ResultEvent {

  MigrationResult getMigrationResult();
}
