package io.mongock.internal.driver;

import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockLockProvider;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  MongockLockProvider getLockProvider();
}
