package io.flamingock.core.core.runner;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

public class RunnerCreator<
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CORE_CONFIG extends CoreConfiguration> {

    public Runner create(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CORE_CONFIG> factory,
                         CORE_CONFIG configuration,
                         EventPublisher eventPublisher,
                         DependencyContext dependencyContext,
                         ExecutionContext executionContext,
                         boolean isThrowExceptionIfCannotObtainLock) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> definitionProcess = factory.getDefinitionProcess(configuration);
        return new AbstractRunner<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS>(
                factory.getLockProvider(),
                factory.getAuditReader(),
                factory.getProcessExecutor(dependencyContext),
                executionContext,
                eventPublisher,
                isThrowExceptionIfCannotObtainLock) {
            @Override
            public void run() {
                this.execute(definitionProcess);
            }
        };
    }
}
