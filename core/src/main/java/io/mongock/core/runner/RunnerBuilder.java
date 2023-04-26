package io.mongock.core.runner;

import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.configuration.LegacyMigration;
import io.mongock.core.configuration.TransactionStrategy;

import java.util.List;
import java.util.Map;

public interface RunnerBuilder<HOLDER, CONFIG extends AbstractConfiguration> {

    Runner build();

    HOLDER setConfiguration(CONFIG configuration);

    HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis);

    HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis);

    HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis);

    HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock);

    HOLDER setTrackIgnored(boolean trackIgnored);

    HOLDER setEnabled(boolean enabled);

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);

    HOLDER setStartSystemVersion(String startSystemVersion);

    HOLDER setEndSystemVersion(String endSystemVersion);

    HOLDER setServiceIdentifier(String serviceIdentifier);

    HOLDER setMetadata(Map<String, Object> metadata);

    HOLDER setLegacyMigration(LegacyMigration legacyMigration);

    HOLDER setTransactionEnabled(Boolean transactionEnabled);

    HOLDER setDefaultMigrationAuthor(String defaultMigrationAuthor);

    HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy);


    ///////////////////////////////////////////////////////////////////////////////////
    //  GETTERS
    ///////////////////////////////////////////////////////////////////////////////////
    CONFIG getConfiguration();

    long getLockAcquiredForMillis();

    Long getLockQuitTryingAfterMillis();

    long getLockTryFrequencyMillis();

    boolean isThrowExceptionIfCannotObtainLock();

    boolean isTrackIgnored();

    boolean isEnabled();

    List<String> getMigrationScanPackage();

    String getStartSystemVersion();

    String getEndSystemVersion();

    String getServiceIdentifier();

    Map<String, Object> getMetadata();

    LegacyMigration getLegacyMigration();

    Boolean getTransactionEnabled();

    String getDefaultMigrationAuthor();

    TransactionStrategy getTransactionStrategy();
}
