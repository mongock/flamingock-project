package io.flamingock.core;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.stage.execution.StageExecutor;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.stage.StageDefinition;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface Factory<CONFIGURATION> {

    LockAcquirer getLockAcquirer();

    SingleAuditReader getAuditReader();
    AuditWriter getAuditWriter();

    StageDefinition getDefinitionProcess(CONFIGURATION config);

    Optional<TransactionWrapper> getTransactionWrapper();

}
