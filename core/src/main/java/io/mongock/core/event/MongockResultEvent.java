package io.mongock.core.event;


import io.mongock.core.event.result.MigrationResult;

public interface MongockResultEvent {

  MigrationResult getMigrationResult();
}
