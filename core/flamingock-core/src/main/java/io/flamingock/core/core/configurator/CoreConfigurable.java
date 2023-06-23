package io.flamingock.core.core.configurator;

import java.util.Map;

public interface CoreConfigurable {
    void setLockAcquiredForMillis(long lockAcquiredForMillis);

    void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis);

    void setLockTryFrequencyMillis(long lockTryFrequencyMillis);

    void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock);

    void setTrackIgnored(boolean trackIgnored);

    void setEnabled(boolean enabled);

    void setStartSystemVersion(String startSystemVersion);

    void setEndSystemVersion(String endSystemVersion);

    void setServiceIdentifier(String serviceIdentifier);

    void setMetadata(Map<String, Object> metadata);

    void setLegacyMigration(LegacyMigration legacyMigration);

    void setTransactionEnabled(Boolean transactionEnabled);

    void setDefaultAuthor(String defaultAuthor);

    void setTransactionStrategy(TransactionStrategy transactionStrategy);

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

    String getDefaultAuthor();

    TransactionStrategy getTransactionStrategy();
}
