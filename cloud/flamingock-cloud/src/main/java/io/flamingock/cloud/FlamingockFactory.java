package io.flamingock.cloud;

import io.flamingock.cloud.lock.FlamingockLockAcquirer;
import io.flamingock.cloud.process.FlamingockDefinitionProcess;
import io.flamingock.cloud.process.FlamingockExecutableProcess;
import io.flamingock.cloud.state.FlamingockAuditProcessStatus;
import io.flamingock.cloud.state.FlamingockAuditReader;
import io.flamingock.cloud.state.FlamingockAuditWriter;
import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

public class FlamingockFactory implements Factory<FlamingockAuditProcessStatus, FlamingockExecutableProcess, FlamingockConfiguration> {

    @Override
    public LockAcquirer<FlamingockAuditProcessStatus, FlamingockExecutableProcess> getLockProvider() {
        return new FlamingockLockAcquirer();
    }

    @Override
    public AuditReader<FlamingockAuditProcessStatus> getAuditReader() {
        return new FlamingockAuditReader();
    }


    @Override
    public DefinitionProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> getDefinitionProcess(FlamingockConfiguration configuration) {
        return new FlamingockDefinitionProcess(configuration);
    }

    @Override
    public ProcessExecutor<FlamingockExecutableProcess> getProcessExecutor(DependencyContext dependencyManager) {
        return null;
    }

    private AuditWriter getStateSaver() {
        return new FlamingockAuditWriter();
    }
}
