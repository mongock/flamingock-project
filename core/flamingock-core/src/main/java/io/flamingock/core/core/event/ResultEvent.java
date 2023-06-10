package io.flamingock.core.core.event;


import io.flamingock.core.core.event.result.MigrationResult;

public interface ResultEvent {

  MigrationResult getMigrationResult();
}
