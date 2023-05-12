package io.mongock.internal;

import io.mongock.core.Factory;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.execution.executor.SingleProcessExecutor;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.single.SingleDefinitionProcess;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.internal.driver.ConnectionEngine;

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
