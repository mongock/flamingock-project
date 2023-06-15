package io.flamingock.core.spring.configurator;

import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.runner.AbstractCoreConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Supplier;

public class DefaultSpringbootConfigurator<HOLDER> implements SpringbootConfigurator<HOLDER> {

    private final Supplier<HOLDER> holderInstanceSupplier;
    private SpringbootConfiguration springbootConfiguration;
    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationContext springContext;

    public DefaultSpringbootConfigurator(SpringbootConfiguration springbootConfiguration,
                                         Supplier<HOLDER> holderInstanceSupplier) {
//        super(coreConfiguration, holderInstanceSupplier);
        this.holderInstanceSupplier = holderInstanceSupplier;
        this.springbootConfiguration = springbootConfiguration;
    }

    public HOLDER setSpringbootConfiguration(SpringbootConfiguration springbootConfiguration) {
        this.springbootConfiguration = springbootConfiguration;
        return holderInstanceSupplier.get();
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

    @Override
    public HOLDER setRunnerType(SpringRunnerType runnerType) {
        springbootConfiguration.setRunnerType(runnerType);
        return holderInstanceSupplier.get();
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfiguration.getRunnerType();
    }

}
