package io.flamingock.core.core.configuration;

import java.util.HashMap;
import java.util.Map;

import static io.flamingock.core.core.util.Constants.DEFAULT_MIGRATION_AUTHOR;

public abstract class CoreConfiguration {

    private final LockConfiguration lockConfiguration  = new LockConfiguration();;
    /**
     * If true, will track ignored changeSets in history. Default false
     */
    private boolean trackIgnored = false;

    /**
     * If false, will disable Mongock. Default true
     */
    private boolean enabled = true;


    /**
     * System version to start with. Default '0'
     */
    private String startSystemVersion = "0";

    /**
     * System version to end with. Default Integer.MAX_VALUE
     */
    private String endSystemVersion = String.valueOf(Integer.MAX_VALUE);

    /**
     * Service identifier.
     */
    private String serviceIdentifier = null;

    /**
     * Map for custom data you want to attach to your migration
     */
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Legacy migration object to instruct Mongock how to import legacy migrations from other tools
     */
    private LegacyMigration legacyMigration = null;

    /**
     * To enable/disable transactions. It works together with the driver, so enabling transactions with a non-transactional
     * driver or a transactional driver with transaction mode off, will throw a MongockException
     */
    private boolean transactionEnabled = true;

    /**
     * From version 5, author is not a mandatory field, but still needed for backward compatibility. This is why Mongock
     * has provided this field, so you can set the author once and forget about it.
     * <p>
     * Default value: default_author
     */
    private String defaultAuthor = DEFAULT_MIGRATION_AUTHOR;

    /**
     * With the introduction of ExecutableChangeUnit in version 5, Mongock provides two strategies to approach the transactions(automatic and manually):
     * - CHANGE_UNIT: Each change unit is wrapped in an independent transaction. This is the default and recommended way for two main reasons:
     * 1. Change Unit provides a method `beforeExecution` which is executed before the transaction when strategy is CHANGE_UNIT.
     * If the strategy is not CHANGE_UNIT, this method is likely to be executed inside the transaction.
     * 2. It maximizes the `eventual completeness` options, as allows Mongock to divide the work in multiple chunks in case all of them together are
     * too big.
     * - EXECUTION: The entire migration's execution is wrapped in a transaction.
     */
    private TransactionStrategy transactionStrategy = TransactionStrategy.CHANGE_UNIT;

    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        lockConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        lockConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        lockConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        lockConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    public void setTrackIgnored(boolean trackIgnored) {
        this.trackIgnored = trackIgnored;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void setStartSystemVersion(String startSystemVersion) {
        this.startSystemVersion = startSystemVersion;
    }

    public void setEndSystemVersion(String endSystemVersion) {
        this.endSystemVersion = endSystemVersion;
    }

    public void setServiceIdentifier(String serviceIdentifier) {
        this.serviceIdentifier = serviceIdentifier;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void setLegacyMigration(LegacyMigration legacyMigration) {
        this.legacyMigration = legacyMigration;
    }

    public void setTransactionEnabled(Boolean transactionEnabled) {
        this.transactionEnabled = transactionEnabled;
    }

    public void setDefaultAuthor(String defaultAuthor) {
        this.defaultAuthor = defaultAuthor;
    }

    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        this.transactionStrategy = transactionStrategy;
    }

    public long getLockAcquiredForMillis() {
        return lockConfiguration.getLockAcquiredForMillis();
    }

    public Long getLockQuitTryingAfterMillis() {
        return lockConfiguration.getLockQuitTryingAfterMillis();
    }

    public long getLockTryFrequencyMillis() {
        return lockConfiguration.getLockTryFrequencyMillis();
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return lockConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    public boolean isTrackIgnored() {
        return trackIgnored;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getStartSystemVersion() {
        return startSystemVersion;
    }

    public String getEndSystemVersion() {
        return endSystemVersion;
    }

    public String getServiceIdentifier() {
        return serviceIdentifier;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public LegacyMigration getLegacyMigration() {
        return legacyMigration;
    }

    public Boolean getTransactionEnabled() {
        return transactionEnabled;
    }

    public String getDefaultAuthor() {
        return defaultAuthor;
    }

    public TransactionStrategy getTransactionStrategy() {
        return transactionStrategy;
    }
}
