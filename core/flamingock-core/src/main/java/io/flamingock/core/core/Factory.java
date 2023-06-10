package io.flamingock.core.core;

import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

public interface Factory<
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIGURATION extends CoreConfiguration> {

    LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getLockProvider();

    AuditReader<AUDIT_PROCESS_STATE> getAuditReader();

    DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getDefinitionProcess(CONFIGURATION config);

    ProcessExecutor<EXECUTABLE_PROCESS> getProcessExecutor(DependencyContext dependencyManager);

}
