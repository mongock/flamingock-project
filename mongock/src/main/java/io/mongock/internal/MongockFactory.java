package io.mongock.internal;

import io.mongock.core.Factory;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.dependency.DependencyManagerImpl;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.execution.executor.SingleProcessExecutor;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.single.SingleDefinitionProcess;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.util.RuntimeHelper;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;

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
    public ProcessExecutor<SingleExecutableProcess> getProcessExecutor() {
        return new SingleProcessExecutor(getAuditWriter());
    }

    private MongockAuditor getAuditWriter() {
        return connectionEngine.getAuditor();
    }
}
