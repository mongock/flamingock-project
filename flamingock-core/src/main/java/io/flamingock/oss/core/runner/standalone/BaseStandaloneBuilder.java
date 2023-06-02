package io.flamingock.oss.core.runner.standalone;

import io.flamingock.oss.core.process.ExecutableProcess;
import io.flamingock.oss.core.runner.BaseBuilder;
import io.flamingock.oss.core.runner.Runner;
import io.flamingock.oss.core.Factory;
import io.flamingock.oss.core.audit.domain.AuditProcessStatus;
import io.flamingock.oss.core.configuration.AbstractConfiguration;
import io.flamingock.oss.core.event.EventPublisher;
import io.flamingock.oss.core.event.MigrationFailureEvent;
import io.flamingock.oss.core.event.MigrationStartedEvent;
import io.flamingock.oss.core.event.MigrationSuccessEvent;
import io.flamingock.oss.core.runtime.dependency.DefaultDependencyInjectableContext;
import io.flamingock.oss.core.runtime.dependency.Dependency;
import io.flamingock.oss.core.runtime.dependency.DependencyInjectableContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseStandaloneBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration>
        extends BaseBuilder<HOLDER, AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG>
        implements StandaloneBuilder<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public BaseStandaloneBuilder(CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
        this(configuration, holderInstanceSupplier, new DefaultDependencyInjectableContext());
    }

    BaseStandaloneBuilder(CONFIG configuration,
                          Supplier<HOLDER> holderInstanceSupplier,
                          DefaultDependencyInjectableContext dependencyManager) {
        super(configuration, holderInstanceSupplier);
        this.dependencyManager = dependencyManager;
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

    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory) {
        EventPublisher eventPublisher = new EventPublisher(
                processStartedListener != null ? () -> processStartedListener.accept(new MigrationStartedEvent()) : null,
                processSuccessListener != null ? result -> processSuccessListener.accept(new MigrationSuccessEvent(result)) : null,
                processFailedListener != null ? result -> processFailedListener.accept(new MigrationFailureEvent(result)) : null);
        return build(factory, eventPublisher, dependencyManager);
    }
}