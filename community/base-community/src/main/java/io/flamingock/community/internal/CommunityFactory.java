package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.Factory;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.stage.StageDefinition;
import io.flamingock.core.stage.execution.SequentialStageExecutor;
import io.flamingock.core.task.filter.TaskFilter;

import java.util.Arrays;

public class CommunityFactory implements Factory<CommunityConfiguration> {
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
    public StageDefinition getDefinitionProcess(CommunityConfiguration configuration) {
        return new StageDefinition(configuration.getMigrationScanPackage()).setFilters(Arrays.asList(filters));
    }

    @Override
    public SequentialStageExecutor getProcessExecutor(DependencyContext dependencyContext) {

        return connectionEngine.getTransactionWrapper()
                .map(transactionWrapper -> new SequentialStageExecutor(dependencyContext, connectionEngine.getAuditor(), transactionWrapper))
                .orElseGet(() -> new SequentialStageExecutor(dependencyContext, connectionEngine.getAuditor()));
    }


}
