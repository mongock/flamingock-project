package io.mongock.internal.driver;

import io.mongock.internal.MongockLockAcquirer;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  MongockLockAcquirer getLockProvider();
}
