package io.flamingock.core.driver;

import io.flamingock.core.audit.Auditor;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface ConnectionEngine {

  void initialize();

  Auditor getAuditor();

  LockAcquirer getLockProvider();

  Optional<TransactionWrapper> getTransactionWrapper();

}
