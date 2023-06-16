package io.flamingock.core.core.configurator;

import java.util.Map;
import java.util.function.Supplier;

public class CoreDelegator<HOLDER> implements CoreConfigurator<HOLDER>{
    private final Supplier<HOLDER> holderSupplier;
    private final CoreProperties properties;

    public CoreDelegator(CoreProperties properties, Supplier<HOLDER> holderSupplier) {
        this.properties = properties;
        this.holderSupplier = holderSupplier;
        
    }

    @Override
    public CoreProperties getCoreProperties() {
        return properties;
    }

    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        properties.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderSupplier.get();
    }

    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        properties.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderSupplier.get();
    }

    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        properties.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderSupplier.get();
    }

    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        properties.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderSupplier.get();
    }

    public HOLDER setTrackIgnored(boolean trackIgnored) {
        properties.setTrackIgnored(trackIgnored);
        return holderSupplier.get();
    }

    public HOLDER setEnabled(boolean enabled) {
        properties.setEnabled(enabled);
        return holderSupplier.get();
    }


    public HOLDER setStartSystemVersion(String startSystemVersion) {
        properties.setStartSystemVersion(startSystemVersion);
        return holderSupplier.get();
    }

    public HOLDER setEndSystemVersion(String endSystemVersion) {
        properties.setEndSystemVersion(endSystemVersion);
        return holderSupplier.get();
    }

    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        properties.setServiceIdentifier(serviceIdentifier);
        return holderSupplier.get();
    }

    public HOLDER setMetadata(Map<String, Object> metadata) {
        properties.setMetadata(metadata);
        return holderSupplier.get();
    }

    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        properties.setLegacyMigration(legacyMigration);
        return holderSupplier.get();
    }

    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        properties.setTransactionEnabled(transactionEnabled);
        return holderSupplier.get();
    }

    public HOLDER setDefaultAuthor(String publicMigrationAuthor) {
        properties.setDefaultAuthor(publicMigrationAuthor);
        return holderSupplier.get();
    }

    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        properties.setTransactionStrategy(transactionStrategy);
        return holderSupplier.get();
    }


    public long getLockAcquiredForMillis() {
        return properties.getLockAcquiredForMillis();
    }

    public Long getLockQuitTryingAfterMillis() {
        return properties.getLockQuitTryingAfterMillis();
    }

    public long getLockTryFrequencyMillis() {
        return properties.getLockTryFrequencyMillis();
    }
    public boolean isThrowExceptionIfCannotObtainLock() {
        return properties.isThrowExceptionIfCannotObtainLock();
    }

    public boolean isTrackIgnored() {
        return properties.isTrackIgnored();
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public String getStartSystemVersion() {
        return properties.getStartSystemVersion();
    }
    public String getEndSystemVersion() {
        return properties.getEndSystemVersion();
    }

    public String getServiceIdentifier() {
        return properties.getServiceIdentifier();
    }
    public Map<String, Object> getMetadata() {
        return properties.getMetadata();
    }

    public LegacyMigration getLegacyMigration() {
        return properties.getLegacyMigration();
    }

    public Boolean getTransactionEnabled() {
        return properties.getTransactionEnabled();
    }

    public String getDefaultAuthor() {
        return properties.getDefaultAuthor();
    }

    public TransactionStrategy getTransactionStrategy() {
        return properties.getTransactionStrategy();
    }
}
