package io.mongock.core.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.mongock.core.util.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.core.util.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.core.util.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

public abstract class AbstractConfiguration {

    /**
     * The period the lock will be reserved once acquired.
     * If it finishes before, it will release it earlier.
     * If the process takes longer thant this period, it will be automatically extended.
     * Default 1 minute.
     * Minimum 3 seconds.
     */
    private long lockAcquiredForMillis = DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;

    /**
     * The time after what Mongock will quit trying to acquire the lock in case it's acquired by another process.
     * Default 3 minutes.
     * Minimum 0, which means won't wait whatsoever.
     */
    private long lockQuitTryingAfterMillis = DEFAULT_QUIT_TRYING_AFTER_MILLIS;

    /**
     * In case the lock is held by another process, it indicates the frequency to try to acquire it.
     * Regardless of this value, the longest Mongock will wait if until the current lock's expiration.
     * Default 1 second.
     * Minimum 500 millis.
     */
    private long lockTryFrequencyMillis = DEFAULT_TRY_FREQUENCY_MILLIS;

    /**
     * Mongock will throw MongockException if lock can not be obtained. Default true
     */
    private boolean throwExceptionIfCannotObtainLock = true;

    /**
     * If true, will track ignored changeSets in history. Default false
     */
    private boolean trackIgnored = false;

    /**
     * If false, will disable Mongock. Default true
     */
    private boolean enabled = true;

    /**
     * Package paths where the changeLogs are located. mandatory
     */
    private List<String> migrationScanPackage = new ArrayList<>();

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
    private Map<String, Object> metadata;

    /**
     * Legacy migration object to instruct Mongock how to import legacy migrations from other tools
     */
    private LegacyMigration legacyMigration = null;

    /**
     * To enable/disable transactions. It works together with the driver, so enabling transactions with a non-transactional
     * driver or a transactional driver with transaction mode off, will throw a MongockException
     */
    private Boolean transactionEnabled;

    /**
     * From version 5, author is not a mandatory field, but still needed for backward compatibility. This is why Mongock
     * has provided this field, so you can set the author once and forget about it.
     *
     * Default value: default_author
     */
    private String defaultAuthor = DEFAULT_MIGRATION_AUTHOR;

    /**
     * With the introduction of ExecutableChangeUnit in version 5, Mongock provides two strategies to approach the transactions(automatic and manually):
     * - CHANGE_UNIT: Each change unit is wrapped in an independent transaction. This is the default and recommended way for two main reasons:
     *                1. Change Unit provides a method `beforeExecution` which is executed before the transaction when strategy is CHANGE_UNIT.
     *                If the strategy is not CHANGE_UNIT, this method is likely to be executed inside the transaction.
     *                2. It maximizes the `eventual completeness` options, as allows Mongock to divide the work in multiple chunks in case all of them together are
     *                too big.
     * - EXECUTION: The entire migration's execution is wrapped in a transaction.
     */
    private TransactionStrategy transactionStrategy = TransactionStrategy.CHANGE_UNIT;


    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        this.lockAcquiredForMillis = lockAcquiredForMillis;
    }

    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    }

    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        this.lockTryFrequencyMillis = lockTryFrequencyMillis;
    }

    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void setTrackIgnored(boolean trackIgnored) {
        this.trackIgnored = trackIgnored;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMigrationScanPackage(List<String> migrationScanPackage) {
        this.migrationScanPackage = migrationScanPackage;
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
        return lockAcquiredForMillis;
    }

    public Long getLockQuitTryingAfterMillis() {
        return lockQuitTryingAfterMillis;
    }

    public long getLockTryFrequencyMillis() {
        return lockTryFrequencyMillis;
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return throwExceptionIfCannotObtainLock;
    }

    public boolean isTrackIgnored() {
        return trackIgnored;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getMigrationScanPackage() {
        return migrationScanPackage;
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
