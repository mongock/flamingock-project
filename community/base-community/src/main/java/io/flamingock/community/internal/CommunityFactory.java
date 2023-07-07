package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.execution.executor.SeqSingleProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.single.SingleDefinitionProcess;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.task.filter.TaskFilter;

public class CommunityFactory implements Factory<SingleAuditProcessStatus, SingleExecutableProcess, CommunityConfiguration> {
    private final ConnectionEngine connectionEngine;
    private final TaskFilter[] filters;

    public CommunityFactory(ConnectionEngine connectionEngine, TaskFilter... filters) {
        this.connectionEngine = connectionEngine;
        this.filters = filters;
    }

    @Override
    public LockAcquirer<SingleAuditProcessStatus, SingleExecutableProcess> getLockProvider() {
        return connectionEngine.getLockProvider();
    }

    @Override
    public AuditReader<SingleAuditProcessStatus> getAuditReader() {
        return connectionEngine.getAuditor();
    }

    @Override
    public DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> getDefinitionProcess(CommunityConfiguration configuration) {
        return new SingleDefinitionProcess(configuration.getMigrationScanPackage(), filters);
    }

    @Override
    public ProcessExecutor<SingleExecutableProcess> getProcessExecutor(DependencyContext dependencyContext) {

        return connectionEngine.getTransactionWrapper()
                .map(transactionWrapper -> new SeqSingleProcessExecutor(dependencyContext, connectionEngine.getAuditor(), transactionWrapper))
                .orElseGet(() -> new SeqSingleProcessExecutor(dependencyContext, connectionEngine.getAuditor()));
    }


}
