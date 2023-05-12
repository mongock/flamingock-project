package io.flamingock.internal;

import io.flamingock.internal.lock.FlamingockLockAcquirer;
import io.flamingock.internal.process.FlamingockDefinitionProcess;
import io.flamingock.internal.process.FlamingockExecutableProcess;
import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.flamingock.internal.state.FlamingockAuditReader;
import io.flamingock.internal.state.FlamingockAuditWriter;
import io.mongock.core.Factory;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;

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
    public ProcessExecutor<FlamingockExecutableProcess> getProcessExecutor(AbstractDependencyManager dependencyManager) {
        return null;
    }

    private AuditWriter<AuditEntry> getStateSaver() {
        return new FlamingockAuditWriter();
    }
}
