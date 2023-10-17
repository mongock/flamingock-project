package io.flamingock.core.configurator.standalone;

import io.flamingock.core.event.model.PipelineCompletedEvent;
import io.flamingock.core.event.model.PipelineFailedEvent;
import io.flamingock.core.event.model.PipelineIgnoredEvent;
import io.flamingock.core.event.model.PipelineStartedEvent;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StandaloneConfiguratorDelegate<HOLDER> implements StandaloneConfigurator<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private final Supplier<HOLDER> holderSupplier;
    private Consumer<PipelineStartedEvent> processStartedListener;
    private Consumer<PipelineCompletedEvent> processSuccessListener;
    private Consumer<PipelineIgnoredEvent> processIgnoredListener;
    private Consumer<PipelineFailedEvent> processFailedListener;

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
    public HOLDER setPipelineStartedListener(Consumer<PipelineStartedEvent> listener) {
        this.processStartedListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setPipelineCompletedListener(Consumer<PipelineCompletedEvent> listener) {
        this.processSuccessListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setPipelineIgnoredListener(Consumer<PipelineIgnoredEvent> listener) {
        this.processIgnoredListener = listener;
        return holderSupplier.get();
    }

    @Override
    public HOLDER setPipelineFailureListener(Consumer<PipelineFailedEvent> listener) {
        this.processFailedListener = listener;
        return holderSupplier.get();
    }

    @Override
    public Consumer<PipelineStartedEvent> getPipelineStartedListener() {
        return processStartedListener;
    }

    @Override
    public Consumer<PipelineCompletedEvent> getPipelineCompletedListener() {
        return processSuccessListener;
    }

    @Override
    public Consumer<PipelineIgnoredEvent> getPipelineIgnoredListener() {
        return processIgnoredListener;
    }

    @Override
    public Consumer<PipelineFailedEvent> getPipelineFailureListener() {
        return processFailedListener;
    }

}
