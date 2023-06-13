package io.flamingock.core.core.runner.standalone;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runner.AbstractBuilder;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.core.runtime.dependency.SimpleDependencyInjectableContext;
import io.flamingock.core.core.runtime.dependency.Dependency;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.core.util.StringUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoreStandaloneBuilderImpl<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CORE_CONFIG extends CoreConfiguration>
        extends AbstractBuilder<HOLDER, CORE_CONFIG>
        implements CoreStandaloneBuilder<HOLDER> {

    private final DependencyInjectableContext dependencyManager;
    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public CoreStandaloneBuilderImpl(CORE_CONFIG coreConfiguration, Supplier<HOLDER> holderInstanceSupplier) {
        this(coreConfiguration, holderInstanceSupplier, new SimpleDependencyInjectableContext());
    }

    CoreStandaloneBuilderImpl(CORE_CONFIG coreConfiguration,
                              Supplier<HOLDER> holderInstanceSupplier,
                              SimpleDependencyInjectableContext dependencyManager) {
        super(coreConfiguration, holderInstanceSupplier);
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


    private ExecutionContext buildExecutionContext() {
        return new ExecutionContext(
                StringUtil.executionId(),
                StringUtil.hostname(),
                getConfiguration().getDefaultAuthor(),
                getConfiguration().getMetadata()
        );
    }

    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CORE_CONFIG> factory) {
        EventPublisher eventPublisher = new EventPublisher(
                processStartedListener != null ? () -> processStartedListener.accept(new MigrationStartedEvent()) : null,
                processSuccessListener != null ? result -> processSuccessListener.accept(new MigrationSuccessEvent(result)) : null,
                processFailedListener != null ? result -> processFailedListener.accept(new MigrationFailureEvent(result)) : null);
        RunnerCreator<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CORE_CONFIG> runnerCreator = new RunnerCreator<>();
        return runnerCreator.create(
                factory,
                getConfiguration(),
                eventPublisher,
                dependencyManager,
                buildExecutionContext(),
                getConfiguration().isThrowExceptionIfCannotObtainLock()
        );
    }
}
