/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.internal.core.builder.core;

import io.flamingock.core.processor.util.Deserializer;
import io.flamingock.internal.common.core.preview.PreviewPipeline;
import io.flamingock.internal.core.builder.TransactionStrategy;

import java.util.HashMap;
import java.util.Map;

import static io.flamingock.internal.util.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.flamingock.internal.util.Constants.DEFAULT_MIGRATION_AUTHOR;
import static io.flamingock.internal.util.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.flamingock.internal.util.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

public class CoreConfiguration implements CoreConfigurable {

    private final LockConfiguration lockConfiguration = new LockConfiguration();

    private final ImporterConfiguration mongockImporterConfiguration = ImporterConfiguration.getDisabled();

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


    public LockConfiguration getLockConfiguration() {
        return lockConfiguration;
    }

    public ImporterConfiguration getMongockImporterConfiguration() {
        return mongockImporterConfiguration;
    }

    @Override
    public PreviewPipeline getPreviewPipeline() {
        return Deserializer.readPreviewPipelineFromFile();
    }


    @Override
    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        lockConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        lockConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        lockConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public void setLockQuitTryingAfterMillis(long lockQuitTryingAfterMillis) {
        lockConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }


    @Override
    public void setEnableRefreshDaemon(boolean enableRefreshDaemon) {
        lockConfiguration.setEnableRefreshDaemon(enableRefreshDaemon);
    }

    @Override
    public boolean isEnableRefreshDaemon() {
        return lockConfiguration.isEnableRefreshDaemon();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public void setStartSystemVersion(String startSystemVersion) {
        this.startSystemVersion = startSystemVersion;
    }

    @Override
    public void setEndSystemVersion(String endSystemVersion) {
        this.endSystemVersion = endSystemVersion;
    }

    @Override
    public void setServiceIdentifier(String serviceIdentifier) {
        this.serviceIdentifier = serviceIdentifier;
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public void setDefaultAuthor(String defaultAuthor) {
        this.defaultAuthor = defaultAuthor;
    }

    @Override
    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        this.transactionStrategy = transactionStrategy;
    }

    @Override
    public long getLockAcquiredForMillis() {
        return lockConfiguration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return lockConfiguration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return lockConfiguration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return lockConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getStartSystemVersion() {
        return startSystemVersion;
    }

    @Override
    public String getEndSystemVersion() {
        return endSystemVersion;
    }

    @Override
    public String getServiceIdentifier() {
        return serviceIdentifier;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String getDefaultAuthor() {
        return defaultAuthor;
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return transactionStrategy;
    }

    @Override
    public void setLegacyMongockChangelogSource(String legacySourceName) {
        mongockImporterConfiguration.setLegacySourceName(legacySourceName);
    }

    @Override
    public String getLegacyMongockChangelogSource() {
        return mongockImporterConfiguration.getLegacySourceName();
    }

    public static class LockConfiguration {

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
         * Flamingock will throw MongockException if lock can not be obtained. Default true
         */
        private boolean throwExceptionIfCannotObtainLock = true;

        /**
         * Flamingock will run a daemon thread in charge of refreshing the lock
         */
        private boolean enableRefreshDaemon = true;


        public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
            this.lockAcquiredForMillis = lockAcquiredForMillis;
        }

        public void setLockQuitTryingAfterMillis(long lockQuitTryingAfterMillis) {
            this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
        }

        public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
            this.lockTryFrequencyMillis = lockTryFrequencyMillis;
        }

        public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
            this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
        }

        public void setEnableRefreshDaemon(boolean enableRefreshDaemon) {
            this.enableRefreshDaemon = enableRefreshDaemon;
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

        public boolean isEnableRefreshDaemon() {
            return enableRefreshDaemon;
        }
    }

    public static class ImporterConfiguration {

        private static ImporterConfiguration getDisabled() {
            return new ImporterConfiguration(null);
        }

        public static ImporterConfiguration withSource(String sourceName) {
            return new ImporterConfiguration(sourceName);
        }

        private String sourceName;

        private ImporterConfiguration(String collectionSourceName) {
            this.sourceName = collectionSourceName;
        }

        public String getLegacySourceName() {
            return sourceName;
        }

        public void setLegacySourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public boolean isEnabled() {
            return sourceName != null;
        }
    }
}
