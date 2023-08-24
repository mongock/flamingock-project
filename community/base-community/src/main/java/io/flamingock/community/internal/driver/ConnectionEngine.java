package io.flamingock.community.internal.driver;

import io.flamingock.core.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface ConnectionEngine {

  void initialize();

  MongockAuditor getAuditor();

  SingleLockAcquirer getLockProvider();

  Optional<TransactionWrapper> getTransactionWrapper();

}
