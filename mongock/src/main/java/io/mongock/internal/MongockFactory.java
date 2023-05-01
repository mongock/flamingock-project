package io.mongock.internal;

import io.mongock.core.Factory;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.dependency.DependencyManagerImpl;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.execution.executor.SingleProcessExecutor;
import io.mongock.core.lock.LockProvider;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.single.SingleDefinitionProcess;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.util.RuntimeHelper;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;

public class MongockFactory implements Factory<SingleAuditProcessStatus, SingleExecutableProcess, MongockConfiguration> {

    public static final RuntimeHelper RUNTIME_HELPER = new RuntimeHelper(new DependencyManagerImpl());
    private final ConnectionEngine connectionEngine;

    MongockFactory(ConnectionEngine connectionEngine) {
        this.connectionEngine = connectionEngine;
    }

    @Override
    public LockProvider<SingleAuditProcessStatus, SingleExecutableProcess> getLockProvider() {
        return connectionEngine.getLockProvider();
    }

    @Override
    public AuditReader<SingleAuditProcessStatus> getAuditReader() {
        return connectionEngine.getAuditor();
    }

    @Override
    public DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> getDefinitionProcess(MongockConfiguration configuration) {
        return new SingleDefinitionProcess(configuration.getScanPackage());
    }

    @Override
    public ProcessExecutor<SingleExecutableProcess> getProcessExecutor() {
        return new SingleProcessExecutor(getAuditWriter(), RUNTIME_HELPER);
    }

    private MongockAuditor getAuditWriter() {
        return connectionEngine.getAuditor();
    }
}
