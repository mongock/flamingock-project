package io.flamingock.core.spring.configurator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Supplier;

public class SpringbootConfiguratorDelegate<HOLDER> implements SpringbootConfigurator<HOLDER> {

    private final Supplier<HOLDER> holderSupplier;
    private final SpringbootConfiguration springbootConfiguration;
    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationContext springContext;

    public SpringbootConfiguratorDelegate(SpringbootConfiguration springbootConfiguration, Supplier<HOLDER> holderSupplier) {
        this.springbootConfiguration = springbootConfiguration;
        this.holderSupplier = holderSupplier;
    }

    @Override
    public HOLDER setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
        return holderSupplier.get();
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springContext;
    }

    @Override
    public HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        return holderSupplier.get();
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return applicationEventPublisher;
    }

    @Override
    public HOLDER setRunnerType(SpringRunnerType runnerType) {
        springbootConfiguration.setRunnerType(runnerType);
        return holderSupplier.get();
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfiguration.getRunnerType();
    }

}
