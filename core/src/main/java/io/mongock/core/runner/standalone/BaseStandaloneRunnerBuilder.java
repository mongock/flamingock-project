package io.mongock.core.runner.standalone;

import io.mongock.core.Factory;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.event.EventPublisher;
import io.mongock.core.event.MigrationFailureEvent;
import io.mongock.core.event.MigrationStartedEvent;
import io.mongock.core.event.MigrationSuccessEvent;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.runner.BaseRunnerBuilder;
import io.mongock.core.runner.Runner;
import io.mongock.core.runner.RunnerOrchestrator;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseStandaloneRunnerBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration>
        extends BaseRunnerBuilder<HOLDER, AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG>
        implements StandaloneRunnerConfigurator<HOLDER> {

    private Consumer<MigrationStartedEvent> processStartedListener;
    private Consumer<MigrationSuccessEvent> processSuccessListener;
    private Consumer<MigrationFailureEvent> processFailedListener;

    public BaseStandaloneRunnerBuilder(CONFIG configuration, Supplier<HOLDER> holderInstanceSupplier) {
        super(configuration, holderInstanceSupplier);
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
    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory,
                        RuntimeHelper.Builder runtimeBuilder) {
        EventPublisher eventPublisher = new EventPublisher(
                processStartedListener != null ? () -> processStartedListener.accept(new MigrationStartedEvent()) : null,
                processSuccessListener != null ? result -> processSuccessListener.accept(new MigrationSuccessEvent(result)) : null,
                processFailedListener != null ? result -> processFailedListener.accept(new MigrationFailureEvent(result)) : null);


        final RunnerOrchestrator<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> runnerOrchestrator = new RunnerOrchestrator<>(
                factory.getLockProvider(),
                factory.getAuditReader(),
                factory.getProcessExecutor(),
                buildExecutionContext(),
                eventPublisher,
                runtimeBuilder,
                getConfiguration().isThrowExceptionIfCannotObtainLock());
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> definitionProcess = factory.getDefinitionProcess(getConfiguration());
        return () -> runnerOrchestrator.execute(definitionProcess);
    }
}
