package io.flamingock.oss.internal;

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
import io.flamingock.oss.internal.driver.ConnectionEngine;

public class MongockFactory implements Factory<SingleAuditProcessStatus, SingleExecutableProcess, MongockConfiguration> {
    private final ConnectionEngine connectionEngine;

    public MongockFactory(ConnectionEngine connectionEngine) {
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
    public DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> getDefinitionProcess(MongockConfiguration configuration) {
        return new SingleDefinitionProcess(configuration.getMigrationScanPackage());
    }

    @Override
    public ProcessExecutor<SingleExecutableProcess> getProcessExecutor(DependencyContext dependencyContext) {

        return connectionEngine.getTransactionWrapper()
                .map(transactionWrapper -> new SingleProcessExecutor(dependencyContext, connectionEngine.getAuditor(), transactionWrapper))
                .orElseGet(() -> new SingleProcessExecutor(dependencyContext, connectionEngine.getAuditor()));
    }


}
