package io.flamingock.core.core.runner;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configurator.CoreConfiguration;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.task.filter.TaskFilter;
import io.flamingock.core.core.util.StringUtil;

import java.util.Collection;

public final class RunnerCreator {

    private RunnerCreator() {
    }

    private static <CORE_CONFIG extends CoreConfiguration> ExecutionContext buildExecutionContext(CORE_CONFIG configuration) {
        return new ExecutionContext(
                StringUtil.executionId(),
                StringUtil.hostname(),
                configuration.getDefaultAuthor(),
                configuration.getMetadata()
        );
    }

    public static <AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess, EXTRA_PROPS>
    Runner create(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, EXTRA_PROPS> factory,
                  CoreConfiguration coreConfiguration,
                  EXTRA_PROPS extraProperties,
                  Collection<TaskFilter> filters,
                  EventPublisher eventPublisher,
                  DependencyContext dependencyContext,
                  boolean isThrowExceptionIfCannotObtainLock) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> definitionProcess = factory.getDefinitionProcess(extraProperties);
        return new AbstractRunner<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS>(
                factory.getLockProvider(),
                factory.getAuditReader(),
                factory.getProcessExecutor(dependencyContext),
                filters,
                buildExecutionContext(coreConfiguration),
                eventPublisher,
                isThrowExceptionIfCannotObtainLock) {
            @Override
            public void run() {
                this.execute(definitionProcess);
            }
        };
    }

}
