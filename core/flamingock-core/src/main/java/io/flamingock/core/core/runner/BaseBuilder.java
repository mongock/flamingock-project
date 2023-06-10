package io.flamingock.core.core.runner;

import io.flamingock.core.core.Factory;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.util.StringUtil;

import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseBuilder<
        HOLDER,
        AUDIT_PROCESS_STATE extends AuditProcessStatus,
        EXECUTABLE_PROCESS extends ExecutableProcess,
        CONFIG extends CoreConfiguration>
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
                           DependencyContext dependencyContext) {
        //Instantiated here, so we don't wait until Runner.run() and fail fast
        final DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> definitionProcess = factory.getDefinitionProcess(getConfiguration());
        return new AbstractRunner<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS>(
                factory.getLockProvider(),
                factory.getAuditReader(),
                factory.getProcessExecutor(dependencyContext),
                buildExecutionContext(),
                eventPublisher,
                getConfiguration().isThrowExceptionIfCannotObtainLock()) {
            @Override
            public void run() {
                this.execute(definitionProcess);
            }
        };
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
