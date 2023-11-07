package io.flamingock.community.runner.springboot.v2;

import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.LegacyMigration;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.springboot.v2.configurator.SpringRunnerType;
import io.flamingock.core.springboot.v2.configurator.SpringbootConfigurable;
import io.flamingock.core.springboot.v2.configurator.SpringbootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("flamingock")
public class CommunitySpringbootConfiguration implements CoreConfigurable, LocalConfigurable, SpringbootConfigurable {

    private final CoreConfiguration coreConfiguration = new CoreConfiguration();

    private final LocalConfiguration communityConfiguration = new LocalConfiguration();

    private final SpringbootConfiguration springbootConfiguration = new SpringbootConfiguration();

    public CoreConfiguration getCoreProperties() {
        return coreConfiguration;
    }

    public LocalConfiguration getCommunityProperties() {
        return communityConfiguration;
    }

    public SpringbootConfiguration getSpringbootProperties() {
        return springbootConfiguration;
    }

    @Override
    public void setStages(List<Stage> stages) {
        coreConfiguration.setStages(stages);
    }

    @Override
    public List<Stage> getStages() {
        return coreConfiguration.getStages();
    }

    @Override
    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public void setTrackIgnored(boolean trackIgnored) {
        coreConfiguration.setTrackIgnored(trackIgnored);
    }

    @Override
    public void setEnabled(boolean enabled) {
        coreConfiguration.setEnabled(enabled);
    }

    @Override
    public void setStartSystemVersion(String startSystemVersion) {
        coreConfiguration.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public void setEndSystemVersion(String endSystemVersion) {
        coreConfiguration.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public void setServiceIdentifier(String serviceIdentifier) {
        coreConfiguration.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        coreConfiguration.setMetadata(metadata);
    }

    @Override
    public void setLegacyMigration(LegacyMigration legacyMigration) {
        coreConfiguration.setLegacyMigration(legacyMigration);
    }

    @Override
    public void setTransactionEnabled(Boolean transactionEnabled) {
        coreConfiguration.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public void setDefaultAuthor(String defaultAuthor) {
        coreConfiguration.setDefaultAuthor(defaultAuthor);
    }

    @Override
    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreConfiguration.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreConfiguration.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguration.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguration.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguration.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguration.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguration.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreConfiguration.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreConfiguration.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguration.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguration.getTransactionStrategy();
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfiguration.getRunnerType();
    }

    @Override
    public void setRunnerType(SpringRunnerType runnerType) {
        springbootConfiguration.setRunnerType(runnerType);
    }



}
