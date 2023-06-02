package io.mongock.internal;

import io.flamingock.oss.core.Factory;
import io.flamingock.oss.core.audit.AuditReader;
import io.flamingock.oss.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.oss.core.execution.executor.ProcessExecutor;
import io.flamingock.oss.core.execution.executor.SingleProcessExecutor;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.process.DefinitionProcess;
import io.flamingock.oss.core.process.single.SingleDefinitionProcess;
import io.flamingock.oss.core.process.single.SingleExecutableProcess;
import io.flamingock.oss.core.runtime.dependency.DependencyContext;
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
