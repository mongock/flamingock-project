package io.mongock.internal.driver;

import io.mongock.internal.MongockLockProvider;

public interface ConnectionEngine {

  void initialize(MongockDriverConfiguration configuration);

  MongockAuditor getAuditor();

  MongockLockProvider getLockProvider();
}
