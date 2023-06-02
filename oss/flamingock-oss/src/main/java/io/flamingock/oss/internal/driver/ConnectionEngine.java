package io.flamingock.oss.internal.driver;

import io.flamingock.core.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  MongockLockAcquirer getLockProvider();

  Optional<TransactionWrapper> getTransactionWrapper();

}
