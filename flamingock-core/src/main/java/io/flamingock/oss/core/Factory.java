package io.flamingock.oss.core;

import io.flamingock.oss.core.audit.AuditReader;
import io.flamingock.oss.core.audit.domain.AuditProcessStatus;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.process.DefinitionProcess;
import io.flamingock.oss.core.process.ExecutableProcess;
import io.flamingock.oss.core.configuration.AbstractConfiguration;
import io.flamingock.oss.core.execution.executor.ProcessExecutor;
import io.flamingock.oss.core.runtime.dependency.DependencyContext;

public interface Factory<
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIGURATION extends AbstractConfiguration> {

    LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getLockProvider();

    AuditReader<AUDIT_PROCESS_STATE> getAuditReader();

    DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> getDefinitionProcess(CONFIGURATION config);

    ProcessExecutor<EXECUTABLE_PROCESS> getProcessExecutor(DependencyContext dependencyManager);

}
