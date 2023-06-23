package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.configurator.LegacyMigration;
import io.flamingock.core.core.configurator.TransactionStrategy;
import io.flamingock.core.spring.configurator.SpringRunnerType;
import io.flamingock.core.spring.configurator.SpringbootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("flamingock")
public class CommunitySpringbootConfigurationProperties {

    private final CoreProperties coreProperties = new CoreProperties();

    private final CommunityProperties communityProperties = new CommunityProperties();

    private final SpringbootProperties springbootProperties = new SpringbootProperties();

    public CoreProperties getCoreProperties() {
        return coreProperties;
    }

    public CommunityProperties getCommunityProperties() {
        return communityProperties;
    }

    public SpringbootProperties getSpringbootProperties() {
        return springbootProperties;
    }

    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreProperties.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreProperties.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreProperties.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreProperties.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    public void setTrackIgnored(boolean trackIgnored) {
        coreProperties.setTrackIgnored(trackIgnored);
    }

    public void setEnabled(boolean enabled) {
        coreProperties.setEnabled(enabled);
    }

    public void setStartSystemVersion(String startSystemVersion) {
        coreProperties.setStartSystemVersion(startSystemVersion);
    }

    public void setEndSystemVersion(String endSystemVersion) {
        coreProperties.setEndSystemVersion(endSystemVersion);
    }

    public void setServiceIdentifier(String serviceIdentifier) {
        coreProperties.setServiceIdentifier(serviceIdentifier);
    }

    public void setMetadata(Map<String, Object> metadata) {
        coreProperties.setMetadata(metadata);
    }

    public void setLegacyMigration(LegacyMigration legacyMigration) {
        coreProperties.setLegacyMigration(legacyMigration);
    }

    public void setTransactionEnabled(Boolean transactionEnabled) {
        coreProperties.setTransactionEnabled(transactionEnabled);
    }

    public void setDefaultAuthor(String defaultAuthor) {
        coreProperties.setDefaultAuthor(defaultAuthor);
    }

    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreProperties.setTransactionStrategy(transactionStrategy);
    }

    public long getLockAcquiredForMillis() {
        return coreProperties.getLockAcquiredForMillis();
    }

    public Long getLockQuitTryingAfterMillis() {
        return coreProperties.getLockQuitTryingAfterMillis();
    }

    public long getLockTryFrequencyMillis() {
        return coreProperties.getLockTryFrequencyMillis();
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreProperties.isThrowExceptionIfCannotObtainLock();
    }

    public boolean isTrackIgnored() {
        return coreProperties.isTrackIgnored();
    }

    public boolean isEnabled() {
        return coreProperties.isEnabled();
    }

    public String getStartSystemVersion() {
        return coreProperties.getStartSystemVersion();
    }

    public String getEndSystemVersion() {
        return coreProperties.getEndSystemVersion();
    }

    public String getServiceIdentifier() {
        return coreProperties.getServiceIdentifier();
    }

    public Map<String, Object> getMetadata() {
        return coreProperties.getMetadata();
    }

    public LegacyMigration getLegacyMigration() {
        return coreProperties.getLegacyMigration();
    }

    public Boolean getTransactionEnabled() {
        return coreProperties.getTransactionEnabled();
    }

    public String getDefaultAuthor() {
        return coreProperties.getDefaultAuthor();
    }

    public TransactionStrategy getTransactionStrategy() {
        return coreProperties.getTransactionStrategy();
    }

    public List<String> getMigrationScanPackage() {
        return communityProperties.getMigrationScanPackage();
    }

    public void setMigrationScanPackage(List<String> migrationScanPackage) {
        communityProperties.setMigrationScanPackage(migrationScanPackage);
    }

    public String getMigrationRepositoryName() {
        return communityProperties.getMigrationRepositoryName();
    }

    public void setMigrationRepositoryName(String value) {
        communityProperties.setMigrationRepositoryName(value);
    }

    public String getLockRepositoryName() {
        return communityProperties.getLockRepositoryName();
    }

    public void setLockRepositoryName(String value) {
        communityProperties.setLockRepositoryName(value);
    }

    public boolean isIndexCreation() {
        return communityProperties.isIndexCreation();
    }

    public void setIndexCreation(boolean value) {
        communityProperties.setIndexCreation(value);
    }

    public SpringRunnerType getRunnerType() {
        return springbootProperties.getRunnerType();
    }

    public void setRunnerType(SpringRunnerType runnerType) {
        springbootProperties.setRunnerType(runnerType);
    }



}
