package io.mongock.core.runner;

import io.mongock.core.Factory;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.configuration.LegacyMigration;
import io.mongock.core.configuration.TransactionStrategy;
import io.mongock.core.event.EventPublisher;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.util.StringUtil;

import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration>
        implements Configurator<HOLDER, CONFIG> {

    private CONFIG configuration;

    protected final Supplier<HOLDER> holderInstanceSupplier;


    public BaseBuilder(CONFIG configuration,
                       Supplier<HOLDER> holderInstanceSupplier) {
        this.configuration = configuration;
        this.holderInstanceSupplier = holderInstanceSupplier;
    }

    protected ExecutionContext buildExecutionContext() {
        return buildExecutionContext(
                StringUtil.executionId(),
                StringUtil.hostname(),
                configuration.getDefaultAuthor(),
                configuration.getMetadata()
        );
    }

    protected ExecutionContext buildExecutionContext(String executionId,
                                                     String hostname,
                                                     String author,
                                                     Map<String, Object> metadata) {
        return new ExecutionContext(executionId, hostname, author, metadata);
    }


    protected Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory,
                           EventPublisher eventPublisher,
                           AbstractDependencyManager dependencyManager) {
        RuntimeHelper.Builder runtimeBuilder = RuntimeHelper
                .builder()
                .setDependencyManager(dependencyManager);
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

    ///////////////////////////////////////////////////////////////////////////////////
    //  SETTERS
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public HOLDER setConfiguration(CONFIG configuration) {
        this.configuration = configuration;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        configuration.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        configuration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        configuration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        configuration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTrackIgnored(boolean trackIgnored) {
        configuration.setTrackIgnored(trackIgnored);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        configuration.setEnabled(enabled);
        return holderInstanceSupplier.get();
    }

    public HOLDER setStartSystemVersion(String startSystemVersion) {
        configuration.setStartSystemVersion(startSystemVersion);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        configuration.setEndSystemVersion(endSystemVersion);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        configuration.setServiceIdentifier(serviceIdentifier);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        configuration.setMetadata(metadata);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        configuration.setLegacyMigration(legacyMigration);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        configuration.setTransactionEnabled(transactionEnabled);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        configuration.setDefaultAuthor(defaultMigrationAuthor);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        configuration.setTransactionStrategy(transactionStrategy);
        return holderInstanceSupplier.get();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  GETTERS
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CONFIG getConfiguration() {
        return configuration;
    }

    @Override
    public long getLockAcquiredForMillis() {
        return configuration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return configuration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return configuration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return configuration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return configuration.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return configuration.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return configuration.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return configuration.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return configuration.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return configuration.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return configuration.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return configuration.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return configuration.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return configuration.getTransactionStrategy();
    }
}
