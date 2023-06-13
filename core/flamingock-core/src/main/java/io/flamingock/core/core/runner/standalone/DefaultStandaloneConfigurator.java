package io.flamingock.core.core.runner.standalone;

import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.runner.AbstractCoreConfigurator;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.runtime.dependency.SimpleDependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.Dependency;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultStandaloneConfigurator<
        HOLDER,
        CORE_CONFIG extends CoreConfiguration>
        extends AbstractCoreConfigurator<HOLDER, CORE_CONFIG>
        implements StandaloneConfigurator<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public DefaultStandaloneConfigurator(CORE_CONFIG coreConfiguration, Supplier<HOLDER> holderInstanceSupplier) {
        this(coreConfiguration, holderInstanceSupplier, new SimpleDependencyInjectableContext());
    }

    DefaultStandaloneConfigurator(CORE_CONFIG coreConfiguration,
                                  Supplier<HOLDER> holderInstanceSupplier,
                                  SimpleDependencyInjectableContext dependencyManager) {
        super(coreConfiguration, holderInstanceSupplier);
        this.dependencyManager = dependencyManager;
    }

    @Override
    public DependencyContext getDependencyContext() {
        return dependencyManager;
    }

    @Override
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        dependencyManager.addDependency(new Dependency(name, type, instance));
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        this.processStartedListener = listener;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        this.processSuccessListener = listener;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        this.processFailedListener = listener;
        return holderInstanceSupplier.get();
    }

    @Override
    public Consumer<MigrationStartedEvent> getMigrationStartedListener() {
        return processStartedListener;
    }

    @Override
    public Consumer<MigrationSuccessEvent> getMigrationSuccessListener() {
        return processSuccessListener;
    }

    @Override
    public Consumer<MigrationFailureEvent> getMigrationFailureListener() {
        return processFailedListener;
    }

}
