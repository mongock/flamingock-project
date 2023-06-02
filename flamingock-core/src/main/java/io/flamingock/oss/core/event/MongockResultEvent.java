package io.flamingock.oss.core.event;


import io.flamingock.oss.core.event.result.MigrationResult;

public interface MongockResultEvent {

  MigrationResult getMigrationResult();
}
