package io.flamingock.core.core.event;


import io.flamingock.core.core.event.result.MigrationResult;

public interface MongockResultEvent {

  MigrationResult getMigrationResult();
}
