package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityConfigurator;
import io.flamingock.community.internal.CommunityConfiguratorImpl;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.runner.CoreConfigurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.spring.SpringDependencyContext;
import io.flamingock.core.spring.builder.DefaultSpringbootConfigurator;
import io.flamingock.core.spring.builder.SpringbootConfigurator;
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

    private final CommunityConfigurator<CommunitySpringbootBuilder, CommunityConfiguration> communityConfigurator;


    CommunitySpringbootBuilder() {
        this(new CommunityConfiguration());
    }

    CommunitySpringbootBuilder(CommunityConfiguration configuration) {
        this.springbootConfigurator = new DefaultSpringbootConfigurator<>(configuration, () -> this);
        this.communityConfigurator = new CommunityConfiguratorImpl<>(configuration, () -> this);
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
                .getConnectionEngine(springbootConfigurator.getConfiguration());
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
    //  CoreSpringbootBuilder
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunitySpringbootBuilder setConfiguration(CommunityConfiguration configuration) {
        communityConfigurator.setConfiguration(configuration);
        springbootConfigurator.setConfiguration(configuration);
        return this;
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return springbootConfigurator.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return springbootConfigurator.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return springbootConfigurator.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return springbootConfigurator.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return springbootConfigurator.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return springbootConfigurator.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return springbootConfigurator.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return springbootConfigurator.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return springbootConfigurator.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return springbootConfigurator.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return springbootConfigurator.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return springbootConfigurator.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return springbootConfigurator.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return springbootConfigurator.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return springbootConfigurator.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return springbootConfigurator.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return springbootConfigurator.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return springbootConfigurator.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return springbootConfigurator.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return springbootConfigurator.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return springbootConfigurator.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return springbootConfigurator.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return springbootConfigurator.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return springbootConfigurator.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return springbootConfigurator.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return springbootConfigurator.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return springbootConfigurator.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return springbootConfigurator.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return springbootConfigurator.getTransactionStrategy();
    }

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

    ///////////////////////////////////////////////////////////////////////////////////
    //  communityRunnerConfigurator
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
}
