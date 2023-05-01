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
import io.mongock.core.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BaseRunnerBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends AbstractConfiguration> {

    private CONFIG configuration;

    private final Supplier<HOLDER> holderInstanceSupplier;

    public BaseRunnerBuilder(CONFIG configuration,
                             Supplier<HOLDER> holderInstanceSupplier) {
        this.configuration = configuration;
        this.holderInstanceSupplier = holderInstanceSupplier;
    }

    public Runner build(Factory<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS, CONFIG> factory) {
        final RunnerOrchestrator<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> runnerOrchestrator = new RunnerOrchestrator<>(
                factory.getLockProvider(),
                factory.getAuditReader(),
                factory.getProcessExecutor(),
                buildExecutionContext(),
                new EventPublisher(),
                configuration.isThrowExceptionIfCannotObtainLock());
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> definitionProcess = factory.getDefinitionProcess(configuration);
        return () -> runnerOrchestrator.execute(definitionProcess);
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

    ///////////////////////////////////////////////////////////////////////////////////
    //  SETTERS
    ///////////////////////////////////////////////////////////////////////////////////

    public HOLDER setConfiguration(CONFIG configuration) {
        this.configuration = configuration;
        return holderInstanceSupplier.get();
    }

    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        configuration.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderInstanceSupplier.get();
    }

    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        configuration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderInstanceSupplier.get();
    }

    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        configuration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderInstanceSupplier.get();
    }

    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        configuration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderInstanceSupplier.get();
    }

    public HOLDER setTrackIgnored(boolean trackIgnored) {
        configuration.setTrackIgnored(trackIgnored);
        return holderInstanceSupplier.get();
    }

    public HOLDER setEnabled(boolean enabled) {
        configuration.setEnabled(enabled);
        return holderInstanceSupplier.get();
    }

    public HOLDER setMigrationScanPackage(List<String> migrationScanPackage) {
        configuration.setMigrationScanPackage(migrationScanPackage);
        return holderInstanceSupplier.get();
    }

    public HOLDER setStartSystemVersion(String startSystemVersion) {
        configuration.setStartSystemVersion(startSystemVersion);
        return holderInstanceSupplier.get();
    }

    public HOLDER setEndSystemVersion(String endSystemVersion) {
        configuration.setEndSystemVersion(endSystemVersion);
        return holderInstanceSupplier.get();
    }

    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        configuration.setServiceIdentifier(serviceIdentifier);
        return holderInstanceSupplier.get();
    }

    public HOLDER setMetadata(Map<String, Object> metadata) {
        configuration.setMetadata(metadata);
        return holderInstanceSupplier.get();
    }

    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        configuration.setLegacyMigration(legacyMigration);
        return holderInstanceSupplier.get();
    }

    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        configuration.setTransactionEnabled(transactionEnabled);
        return holderInstanceSupplier.get();
    }

    public HOLDER setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        configuration.setDefaultAuthor(defaultMigrationAuthor);
        return holderInstanceSupplier.get();
    }

    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        configuration.setTransactionStrategy(transactionStrategy);
        return holderInstanceSupplier.get();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  GETTERS
    ///////////////////////////////////////////////////////////////////////////////////
    public CONFIG getConfiguration() {
        return configuration;
    }

    public long getLockAcquiredForMillis() {
        return configuration.getLockAcquiredForMillis();
    }

    public Long getLockQuitTryingAfterMillis() {
        return configuration.getLockQuitTryingAfterMillis();
    }

    public long getLockTryFrequencyMillis() {
        return configuration.getLockTryFrequencyMillis();
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return configuration.isThrowExceptionIfCannotObtainLock();
    }

    public boolean isTrackIgnored() {
        return configuration.isTrackIgnored();
    }

    public boolean isEnabled() {
        return configuration.isEnabled();
    }

    public List<String> getMigrationScanPackage() {
        return configuration.getMigrationScanPackage();
    }

    public String getStartSystemVersion() {
        return configuration.getStartSystemVersion();
    }

    public String getEndSystemVersion() {
        return configuration.getEndSystemVersion();
    }

    public String getServiceIdentifier() {
        return configuration.getServiceIdentifier();
    }

    public Map<String, Object> getMetadata() {
        return configuration.getMetadata();
    }

    public LegacyMigration getLegacyMigration() {
        return configuration.getLegacyMigration();
    }

    public Boolean getTransactionEnabled() {
        return configuration.getTransactionEnabled();
    }

    public String getDefaultMigrationAuthor() {
        return configuration.getDefaultAuthor();
    }

    public TransactionStrategy getTransactionStrategy() {
        return configuration.getTransactionStrategy();
    }
}
