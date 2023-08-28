package io.flamingock.core;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.stage.DefinitionStage;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface Factory<CONFIGURATION> {

    LockAcquirer getLockAcquirer();

    SingleAuditReader getAuditReader();
    AuditWriter getAuditWriter();

    DefinitionStage getDefinitionProcess(CONFIGURATION config);

    Optional<TransactionWrapper> getTransactionWrapper();

}
