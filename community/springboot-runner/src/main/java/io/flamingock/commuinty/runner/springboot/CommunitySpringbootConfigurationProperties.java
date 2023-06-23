package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.configurator.ICoreProperties;
import io.flamingock.core.core.configurator.LegacyMigration;
import io.flamingock.core.core.configurator.TransactionStrategy;
import io.flamingock.core.spring.configurator.SpringRunnerType;
import io.flamingock.core.spring.configurator.SpringbootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("flamingock")
public class CommunitySpringbootConfigurationProperties implements ICoreProperties {

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

    @Override
    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreProperties.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreProperties.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreProperties.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreProperties.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public void setTrackIgnored(boolean trackIgnored) {
        coreProperties.setTrackIgnored(trackIgnored);
    }

    @Override
    public void setEnabled(boolean enabled) {
        coreProperties.setEnabled(enabled);
    }

    @Override
    public void setStartSystemVersion(String startSystemVersion) {
        coreProperties.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public void setEndSystemVersion(String endSystemVersion) {
        coreProperties.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public void setServiceIdentifier(String serviceIdentifier) {
        coreProperties.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        coreProperties.setMetadata(metadata);
    }

    @Override
    public void setLegacyMigration(LegacyMigration legacyMigration) {
        coreProperties.setLegacyMigration(legacyMigration);
    }

    @Override
    public void setTransactionEnabled(Boolean transactionEnabled) {
        coreProperties.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public void setDefaultAuthor(String defaultAuthor) {
        coreProperties.setDefaultAuthor(defaultAuthor);
    }

    @Override
    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreProperties.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreProperties.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreProperties.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreProperties.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreProperties.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreProperties.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreProperties.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreProperties.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreProperties.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreProperties.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreProperties.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreProperties.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreProperties.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreProperties.getDefaultAuthor();
    }

    @Override
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
