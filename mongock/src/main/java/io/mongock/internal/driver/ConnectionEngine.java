package io.mongock.internal.driver;

import io.mongock.internal.MongockLockProvider;

public interface ConnectionEngine {

  void initialize(String executionId, MongockDriverConfiguration configuration);

  MongockAuditor getAuditor();

  MongockLockProvider getLockProvider();
}
