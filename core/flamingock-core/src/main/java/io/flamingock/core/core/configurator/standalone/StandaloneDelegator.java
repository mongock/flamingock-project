package io.flamingock.core.core.configurator.standalone;

import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.runtime.dependency.SimpleDependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.Dependency;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StandaloneDelegator<HOLDER> implements StandaloneConfigurator<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private final Supplier<HOLDER> holderSupplier;
    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public StandaloneDelegator(DependencyInjectableContext dependencyManager, Supplier<HOLDER> holderSupplier) {
        this.dependencyManager = dependencyManager;
        this.holderSupplier = holderSupplier;
    }

    
    public DependencyContext getDependencyContext() {
        return dependencyManager;
    }

    
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        dependencyManager.addDependency(new Dependency(name, type, instance));
        return holderSupplier.get();
    }


    public HOLDER addDependency(Object instance) {
        return addDependency(Dependency.DEFAULT_NAME, instance.getClass(), instance);
    }

    public HOLDER addDependency(String name, Object instance) {
        return addDependency(name, instance.getClass(), instance);
    }

    public HOLDER addDependency(Class<?> type, Object instance) {
        return addDependency(Dependency.DEFAULT_NAME, type, instance);
    }

    
    public HOLDER setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        this.processStartedListener = listener;
        return holderSupplier.get();
    }

    
    public HOLDER setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        this.processSuccessListener = listener;
        return holderSupplier.get();
    }

    
    public HOLDER setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        this.processFailedListener = listener;
        return holderSupplier.get();
    }

    
    public Consumer<MigrationStartedEvent> getMigrationStartedListener() {
        return processStartedListener;
    }

    
    public Consumer<MigrationSuccessEvent> getMigrationSuccessListener() {
        return processSuccessListener;
    }

    
    public Consumer<MigrationFailureEvent> getMigrationFailureListener() {
        return processFailedListener;
    }

}
