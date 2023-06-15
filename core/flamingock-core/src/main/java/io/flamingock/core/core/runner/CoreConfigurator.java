package io.flamingock.core.core.runner;

import io.flamingock.core.core.configuration.CoreConfiguration;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;

import java.util.Map;

public interface CoreConfigurator<HOLDER, CONFIG extends CoreConfiguration> {


    HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis);

    HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis);

    HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis);

    HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock);

    HOLDER setTrackIgnored(boolean trackIgnored);

    HOLDER setEnabled(boolean enabled);


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

    String getStartSystemVersion();

    String getEndSystemVersion();

    String getServiceIdentifier();

    Map<String, Object> getMetadata();

    LegacyMigration getLegacyMigration();

    Boolean getTransactionEnabled();

    String getDefaultMigrationAuthor();

    TransactionStrategy getTransactionStrategy();
}
