package io.mongock.internal.driver;

import io.mongock.core.transaction.TransactionWrapper;
import io.mongock.internal.MongockLockAcquirer;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  MongockLockAcquirer getLockProvider();

  TransactionWrapper getTransactionWrapper();
}
