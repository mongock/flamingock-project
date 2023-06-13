package io.flamingock.core.spring.builder;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runner.AbstractCoreConfigurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.core.util.StringUtil;
import io.flamingock.core.spring.SpringDependencyContext;
import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import io.flamingock.core.spring.event.SpringMigrationStartedEvent;
import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Supplier;

public class CoreSpringbootBuilderImpl<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CORE_CONFIG extends CoreConfiguration>
        extends AbstractCoreConfigurator<HOLDER, CORE_CONFIG>
        implements CoreSpringbootBuilder<HOLDER> {

    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationContext springContext;

    public CoreSpringbootBuilderImpl(CORE_CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
        super(configuration, holderInstanceSupplier);
    }

    @Override
    public HOLDER setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        return holderInstanceSupplier.get();
    }

    private ExecutionContext buildExecutionContext() {
        return new ExecutionContext(
                StringUtil.executionId(),
                StringUtil.hostname(),
                getConfiguration().getDefaultAuthor(),
                getConfiguration().getMetadata()
        );
    }

    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CORE_CONFIG> factory) {
        EventPublisher eventPublisher = new EventPublisher(
                () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
                result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
                result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
        );
        RunnerCreator<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CORE_CONFIG> runnerCreator = new RunnerCreator<>();
        return runnerCreator.create(
                factory,
                getConfiguration(),
                eventPublisher,
                new SpringDependencyContext(springContext),
                buildExecutionContext(),
                getConfiguration().isThrowExceptionIfCannotObtainLock()
        );
    }

}
