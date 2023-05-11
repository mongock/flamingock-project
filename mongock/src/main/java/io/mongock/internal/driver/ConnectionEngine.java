package io.mongock.internal.driver;

import io.mongock.core.transaction.TransactionWrapper;
import io.mongock.internal.MongockLockAcquirer;

import java.util.Optional;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  MongockLockAcquirer getLockProvider();

  Optional<TransactionWrapper> getTransactionWrapper();

}
