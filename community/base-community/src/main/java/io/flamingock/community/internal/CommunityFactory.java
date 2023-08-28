package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.Factory;
import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.stage.DefinitionStage;
import io.flamingock.core.task.filter.TaskFilter;
import io.flamingock.core.transaction.TransactionWrapper;

import java.util.Arrays;
import java.util.Optional;

public class CommunityFactory implements Factory {
    private final ConnectionEngine connectionEngine;
    private final TaskFilter[] filters;

    public CommunityFactory(ConnectionEngine connectionEngine, TaskFilter... filters) {
        this.connectionEngine = connectionEngine;
        this.filters = filters;
    }

    @Override
    public LockAcquirer getLockAcquirer() {
        return connectionEngine.getLockProvider();
    }

    @Override
    public SingleAuditReader getAuditReader() {
        return connectionEngine.getAuditor();
    }

    @Override
    public AuditWriter getAuditWriter() {
        return connectionEngine.getAuditor();
    }

    @Override
    public DefinitionStage getDefinitionProcess(CoreConfiguration configuration) {
        return new DefinitionStage(configuration.getMigrationScanPackage()).setFilters(Arrays.asList(filters));
    }

    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return connectionEngine.getTransactionWrapper();
    }


}
