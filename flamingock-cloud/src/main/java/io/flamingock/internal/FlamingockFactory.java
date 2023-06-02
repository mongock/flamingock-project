package io.flamingock.internal;

import io.flamingock.internal.lock.FlamingockLockAcquirer;
import io.flamingock.internal.process.FlamingockDefinitionProcess;
import io.flamingock.internal.process.FlamingockExecutableProcess;
import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.flamingock.internal.state.FlamingockAuditReader;
import io.flamingock.internal.state.FlamingockAuditWriter;
import io.flamingock.oss.core.Factory;
import io.flamingock.oss.core.audit.AuditReader;
import io.flamingock.oss.core.audit.domain.AuditEntry;
import io.flamingock.oss.core.audit.writer.AuditWriter;
import io.flamingock.oss.core.execution.executor.ProcessExecutor;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.process.DefinitionProcess;
import io.flamingock.oss.core.runtime.dependency.DependencyContext;

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

    private AuditWriter<AuditEntry> getStateSaver() {
        return new FlamingockAuditWriter();
    }
}
