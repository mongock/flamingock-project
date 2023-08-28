package io.flamingock.core.configurator;

import java.util.List;
import java.util.Map;

public interface CoreConfigurator<HOLDER> {

    CoreConfiguration getCoreProperties();

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

    HOLDER setDefaultAuthor(String defaultMigrationAuthor);

    HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy);

    List<String> getMigrationScanPackage();

    HOLDER addMigrationScanPackages(List<String> migrationScanPackageList);

    HOLDER addMigrationScanPackage(String migrationScanPackage);

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);

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
