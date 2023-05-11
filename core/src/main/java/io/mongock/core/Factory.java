package io.mongock.core;

import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.transaction.TransactionWrapper;

import java.util.Optional;

public interface Factory<
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIGURATION extends AbstractConfiguration> {

    LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getLockProvider();

    AuditReader<AUDIT_PROCESS_STATE> getAuditReader();

    DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getDefinitionProcess(CONFIGURATION config);

    ProcessExecutor<EXECUTABLE_PROCESS> getProcessExecutor();

}
