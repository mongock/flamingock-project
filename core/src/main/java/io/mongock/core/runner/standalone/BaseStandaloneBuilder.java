package io.mongock.core.runner.standalone;

import io.mongock.core.Factory;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.event.EventPublisher;
import io.mongock.core.event.MigrationFailureEvent;
import io.mongock.core.event.MigrationStartedEvent;
import io.mongock.core.event.MigrationSuccessEvent;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.runner.BaseBuilder;
import io.mongock.core.runner.Runner;
import io.mongock.core.runtime.dependency.DefaultDependencyManager;
import io.mongock.core.runtime.dependency.Dependency;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseStandaloneBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration>
        extends BaseBuilder<HOLDER, AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG>
        implements StandaloneBuilder<HOLDER> {

    private final DefaultDependencyManager dependencyManager;
    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public BaseStandaloneBuilder(CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
        this(configuration, holderInstanceSupplier, new DefaultDependencyManager());
    }

    BaseStandaloneBuilder(CONFIG configuration,
                          Supplier<HOLDER> holderInstanceSupplier,
                          DefaultDependencyManager dependencyManager) {
        super(configuration, holderInstanceSupplier);
        this.dependencyManager = dependencyManager;
    }

    @Override
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        dependencyManager.addStandardDependency(new Dependency(name, type, instance));
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
        return super.build(factory, eventPublisher, dependencyManager);
    }
}
