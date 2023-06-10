package io.flamingock.core.spring.builder;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runner.BaseBuilder;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.spring.SpringDependencyContext;
import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import io.flamingock.core.spring.event.SpringMigrationStartedEvent;
import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Supplier;

public class BaseSpringBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends CoreConfiguration>
        extends BaseBuilder<HOLDER, AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG>
        implements SpringBuilder<HOLDER> {

    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationContext springContext;

    public BaseSpringBuilder(CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
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

    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory) {
        EventPublisher eventPublisher = new EventPublisher(
                () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
                result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
                result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
        );
        return build(factory, eventPublisher, new SpringDependencyContext(springContext));
    }
}
