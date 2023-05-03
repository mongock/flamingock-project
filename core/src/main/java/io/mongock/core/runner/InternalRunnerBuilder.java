package io.mongock.core.runner;

import io.mongock.core.Factory;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.runtime.dependency.DependencyManager;
import io.mongock.core.process.ExecutableProcess;

public interface InternalRunnerBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration>
        extends RunnerConfigurator<HOLDER, CONFIG> {

    Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory, RuntimeHelper.LockableBuilder runtimeBuilder);
}
