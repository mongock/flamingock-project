package io.flamingock.core.spring.builder;

import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.runner.AbstractCoreConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Supplier;

public class DefaultSpringbootConfigurator<
        HOLDER,
        CORE_CONFIG extends CoreConfiguration>
        extends AbstractCoreConfigurator<HOLDER, CORE_CONFIG>
        implements SpringbootConfigurator<HOLDER> {

    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationContext springContext;

    public DefaultSpringbootConfigurator(CORE_CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
        super(configuration, holderInstanceSupplier);
    }

    @Override
    public HOLDER setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
        return holderInstanceSupplier.get();
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springContext;
    }

    @Override
    public HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        return holderInstanceSupplier.get();
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return applicationEventPublisher;
    }

}
