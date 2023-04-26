package io.flamingock.internal;

import io.flamingock.internal.lock.FlamingockLockProvider;
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
import io.mongock.core.lock.LockProvider;
import io.mongock.core.process.DefinitionProcess;

public class FlamingockFactory implements Factory<FlamingockAuditProcessStatus, FlamingockExecutableProcess, FlamingockConfiguration> {
    @Override
    public LockProvider<FlamingockAuditProcessStatus, FlamingockExecutableProcess> getLockProvider() {
        return new FlamingockLockProvider();
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
    public ProcessExecutor<FlamingockExecutableProcess> getProcessExecutor() {
        return null;
    }

    private AuditWriter<AuditEntry> getStateSaver() {
        return new FlamingockAuditWriter();
    }
}
