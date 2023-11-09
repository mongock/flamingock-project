package io.flamingock.core.configurator;

import io.flamingock.core.configurator.legacy.LegacyMigration;
import io.flamingock.core.pipeline.Stage;

import java.util.List;
import java.util.Map;

public interface CoreConfigurable {

    void setStages(List<Stage> stages);

    List<Stage> getStages();

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
