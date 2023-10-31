package io.flamingock.community.internal.driver;

import io.flamingock.core.audit.Auditor;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface ConnectionEngine {

  void initialize();

  Auditor getAuditor();

  LocalLockAcquirer getLockProvider();

  Optional<TransactionWrapper> getTransactionWrapper();

}
