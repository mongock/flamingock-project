package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.single.SingleAuditReader;
import io.flamingock.core.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.core.execution.executor.SeqSingleProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.StageDefinition;
import io.flamingock.core.core.task.filter.TaskFilter;

import java.util.Arrays;

public class CommunityFactory implements Factory<SingleAuditStageStatus, ExecutableStage, CommunityConfiguration> {
    private final ConnectionEngine connectionEngine;
    private final TaskFilter[] filters;

    public CommunityFactory(ConnectionEngine connectionEngine, TaskFilter... filters) {
        this.connectionEngine = connectionEngine;
        this.filters = filters;
    }

    @Override
    public LockAcquirer<SingleAuditStageStatus, ExecutableStage> getLockProvider() {
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
    public SeqSingleProcessExecutor getProcessExecutor(DependencyContext dependencyContext) {

        return connectionEngine.getTransactionWrapper()
                .map(transactionWrapper -> new SeqSingleProcessExecutor(dependencyContext, connectionEngine.getAuditor(), transactionWrapper))
                .orElseGet(() -> new SeqSingleProcessExecutor(dependencyContext, connectionEngine.getAuditor()));
    }


}
