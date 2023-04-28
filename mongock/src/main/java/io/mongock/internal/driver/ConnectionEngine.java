package io.mongock.internal.driver;

import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockLockProvider;

public interface ConnectionEngine<CONFIGURATION extends MongockConfiguration> {

  void initialize(CONFIGURATION configuration);

  MongockAuditor getAuditor();

  MongockLockProvider getLockProvider();
}
