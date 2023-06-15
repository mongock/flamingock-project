package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityConfigurator;
import io.flamingock.community.internal.DefaultCommunityConfigurator;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.runner.CoreConfigurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.spring.SpringDependencyContext;
import io.flamingock.core.spring.configurator.DefaultSpringbootConfigurator;
import io.flamingock.core.spring.configurator.SpringRunnerType;
import io.flamingock.core.spring.configurator.SpringbootConfiguration;
import io.flamingock.core.spring.configurator.SpringbootConfigurator;
import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import io.flamingock.core.spring.event.SpringMigrationStartedEvent;
import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;

public class CommunitySpringbootBuilder
        implements
        SpringRunnerBuilder,
        CommunityConfigurator<CommunitySpringbootBuilder, CommunityConfiguration>,
        SpringbootConfigurator<CommunitySpringbootBuilder>,
        CoreConfigurator<CommunitySpringbootBuilder, CommunityConfiguration> {

    private final DefaultSpringbootConfigurator<CommunitySpringbootBuilder, CommunityConfiguration> springbootConfigurator;

    private final DefaultCommunityConfigurator<CommunitySpringbootBuilder, CommunityConfiguration> communityConfigurator;


    CommunitySpringbootBuilder() {
        this(new CommunityConfiguration(), new SpringbootConfiguration());
    }

    CommunitySpringbootBuilder(CommunityConfiguration communityConfiguration, SpringbootConfiguration springbootConfiguration) {
        this.springbootConfigurator = new DefaultSpringbootConfigurator<>(communityConfiguration, springbootConfiguration, () -> this);
        this.communityConfigurator = new DefaultCommunityConfigurator<>(communityConfiguration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        EventPublisher eventPublisher = new EventPublisher(
                () -> getEventPublisher().publishEvent(new SpringMigrationStartedEvent(this)),
                result -> getEventPublisher().publishEvent(new SpringMigrationSuccessEvent(this, result)),
                result -> getEventPublisher().publishEvent(new SpringMigrationFailureEvent(this, result))
        );

        ConnectionEngine connectionEngine = communityConfigurator
                .getDriver()
                .getConnectionEngine(communityConfigurator.getConfiguration());
        connectionEngine.initialize();
        return RunnerCreator.create(
                new CommunityFactory(connectionEngine),
                getConfiguration(),
                eventPublisher,
                new SpringDependencyContext(getSpringContext()),
                getConfiguration().isThrowExceptionIfCannotObtainLock()
        );

    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunitySpringbootBuilder setRunnerType(SpringRunnerType runnerType) {
        return springbootConfigurator.setRunnerType(runnerType);
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfigurator.getRunnerType();
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return communityConfigurator.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return communityConfigurator.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return communityConfigurator.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return communityConfigurator.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return communityConfigurator.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return communityConfigurator.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return communityConfigurator.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return communityConfigurator.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return communityConfigurator.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return communityConfigurator.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return communityConfigurator.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return communityConfigurator.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return communityConfigurator.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return communityConfigurator.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return communityConfigurator.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return communityConfigurator.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return communityConfigurator.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return communityConfigurator.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return communityConfigurator.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return communityConfigurator.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return communityConfigurator.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return communityConfigurator.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return communityConfigurator.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return communityConfigurator.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return communityConfigurator.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return communityConfigurator.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return communityConfigurator.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return communityConfigurator.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return communityConfigurator.getTransactionStrategy();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  COMMUNITY
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunitySpringbootBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return communityConfigurator.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return communityConfigurator.getDriver();
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return communityConfigurator.getMigrationScanPackage();
    }

    @Override
    public CommunitySpringbootBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityConfigurator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityConfigurator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityConfigurator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityConfigurator.getMigrationRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setMigrationRepositoryName(String value) {
        return communityConfigurator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityConfigurator.getLockRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setLockRepositoryName(String value) {
        return communityConfigurator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityConfigurator.isIndexCreation();
    }

    @Override
    public CommunitySpringbootBuilder setIndexCreation(boolean value) {
        return communityConfigurator.setIndexCreation(value);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  SPRINGBOOT
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return springbootConfigurator.setSpringContext(springContext);
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springbootConfigurator.getSpringContext();
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return springbootConfigurator.setEventPublisher(applicationEventPublisher);
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return springbootConfigurator.getEventPublisher();
    }

}
