package io.flamingock.core.core.runner;

import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractCoreConfigurator<
        HOLDER,
        CORE_CONFIG extends CoreConfiguration>
        implements CoreConfigurator<HOLDER, CORE_CONFIG> {

    private CORE_CONFIG coreConfiguration;

    protected final Supplier<HOLDER> holderInstanceSupplier;


    public AbstractCoreConfigurator(CORE_CONFIG coreConfiguration,
                                    Supplier<HOLDER> holderInstanceSupplier) {
        this.coreConfiguration = coreConfiguration;
        this.holderInstanceSupplier = holderInstanceSupplier;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  SETTERS
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public HOLDER setConfiguration(CORE_CONFIG configuration) {
        this.coreConfiguration = configuration;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTrackIgnored(boolean trackIgnored) {
        coreConfiguration.setTrackIgnored(trackIgnored);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        coreConfiguration.setEnabled(enabled);
        return holderInstanceSupplier.get();
    }

    public HOLDER setStartSystemVersion(String startSystemVersion) {
        coreConfiguration.setStartSystemVersion(startSystemVersion);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        coreConfiguration.setEndSystemVersion(endSystemVersion);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        coreConfiguration.setServiceIdentifier(serviceIdentifier);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        coreConfiguration.setMetadata(metadata);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        coreConfiguration.setLegacyMigration(legacyMigration);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        coreConfiguration.setTransactionEnabled(transactionEnabled);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        coreConfiguration.setDefaultAuthor(defaultMigrationAuthor);
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreConfiguration.setTransactionStrategy(transactionStrategy);
        return holderInstanceSupplier.get();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  GETTERS
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CORE_CONFIG getConfiguration() {
        return coreConfiguration;
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreConfiguration.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguration.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguration.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguration.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguration.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguration.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreConfiguration.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreConfiguration.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return coreConfiguration.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguration.getTransactionStrategy();
    }



}
