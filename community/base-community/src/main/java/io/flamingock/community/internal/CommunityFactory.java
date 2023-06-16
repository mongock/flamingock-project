package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.execution.executor.SingleProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.single.SingleDefinitionProcess;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

public class CommunityFactory implements Factory<SingleAuditProcessStatus, SingleExecutableProcess, CommunityProperties> {
    private final ConnectionEngine connectionEngine;

    public CommunityFactory(ConnectionEngine connectionEngine) {
        this.connectionEngine = connectionEngine;
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
    public DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> getDefinitionProcess(CommunityProperties configuration) {
        return new SingleDefinitionProcess(configuration.getMigrationScanPackage());
    }

    @Override
    public ProcessExecutor<SingleExecutableProcess> getProcessExecutor(DependencyContext dependencyContext) {

        return connectionEngine.getTransactionWrapper()
                .map(transactionWrapper -> new SingleProcessExecutor(dependencyContext, connectionEngine.getAuditor(), transactionWrapper))
                .orElseGet(() -> new SingleProcessExecutor(dependencyContext, connectionEngine.getAuditor()));
    }


}
