package io.flamingock.core.configurator.standalone;

import io.flamingock.core.event.model.CompletedEvent;
import io.flamingock.core.event.model.FailedEvent;
import io.flamingock.core.event.model.IgnoredEvent;
import io.flamingock.core.event.model.StartedEvent;
import io.flamingock.core.event.model.SuccessEvent;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StandaloneConfiguratorDelegate<HOLDER> implements StandaloneConfigurator<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private final Supplier<HOLDER> holderSupplier;
    private Consumer<StartedEvent> processStartedListener;
    private Consumer<CompletedEvent> processSuccessListener;
    private Consumer<IgnoredEvent> processIgnoredListener;
    private Consumer<FailedEvent> processFailedListener;

    public StandaloneConfiguratorDelegate(DependencyInjectableContext dependencyManager, Supplier<HOLDER> holderSupplier) {
        this.dependencyManager = dependencyManager;
        this.holderSupplier = holderSupplier;
    }

    @Override
    public DependencyContext getDependencyContext() {
        return dependencyManager;
    }

    @Override
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        dependencyManager.addDependency(new Dependency(name, type, instance));
        return holderSupplier.get();
    }

    @Override
    public HOLDER addDependency(Object instance) {
        return addDependency(Dependency.DEFAULT_NAME, instance.getClass(), instance);
    }

    @Override
    public HOLDER addDependency(String name, Object instance) {
        return addDependency(name, instance.getClass(), instance);
    }

    @Override
    public HOLDER addDependency(Class<?> type, Object instance) {
        return addDependency(Dependency.DEFAULT_NAME, type, instance);
    }

    @Override
    public HOLDER setMigrationStartedListener(Consumer<StartedEvent> listener) {
        this.processStartedListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setMigrationSuccessListener(Consumer<CompletedEvent> listener) {
        this.processSuccessListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setPipelineIgnoredListener(Consumer<IgnoredEvent> listener) {
        this.processIgnoredListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setMigrationFailureListener(Consumer<FailedEvent> listener) {
        this.processFailedListener = listener;
        return holderSupplier.get();
    }

    @Override
    public Consumer<StartedEvent> getMigrationStartedListener() {
        return processStartedListener;
    }

    @Override
    public Consumer<CompletedEvent> getMigrationSuccessListener() {
        return processSuccessListener;
    }

    @Override
    public Consumer<IgnoredEvent> getPipelineIgnoredListener() {
        return processIgnoredListener;
    }

    @Override
    public Consumer<FailedEvent> getMigrationFailureListener() {
        return processFailedListener;
    }

}
