package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.CommunityConfigurator;
import io.flamingock.community.internal.CommunityDelegator;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.configurator.CoreConfigurator;
import io.flamingock.core.core.configurator.CoreDelegator;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.configurator.LegacyMigration;
import io.flamingock.core.core.configurator.TransactionStrategy;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.spring.SpringDependencyContext;
import io.flamingock.core.spring.configurator.SpringRunnerType;
import io.flamingock.core.spring.configurator.SpringbootConfigurator;
import io.flamingock.core.spring.configurator.SpringbootDelegator;
import io.flamingock.core.spring.configurator.SpringbootProperties;
import io.flamingock.core.spring.event.SpringMigrationFailureEvent;
import io.flamingock.core.spring.event.SpringMigrationStartedEvent;
import io.flamingock.core.spring.event.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;

public class CommunitySpringbootBuilder
        implements
        CoreConfigurator<CommunitySpringbootBuilder>,
        CommunityConfigurator<CommunitySpringbootBuilder>,
        SpringbootConfigurator<CommunitySpringbootBuilder>,
        SpringRunnerBuilder {


    private final CoreDelegator<CommunitySpringbootBuilder> coreDelegator;

    private final CommunityDelegator<CommunitySpringbootBuilder> communityDelegator;

    private final SpringbootDelegator<CommunitySpringbootBuilder> springbootDelegator;


    CommunitySpringbootBuilder(CoreProperties coreProperties,
                               CommunityProperties communityProperties,
                               SpringbootProperties springbootProperties) {
        this.coreDelegator = new CoreDelegator<>(coreProperties, () -> this);
        this.communityDelegator = new CommunityDelegator<>(communityProperties, () -> this);
        this.springbootDelegator = new SpringbootDelegator<>(springbootProperties, () -> this);
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

        ConnectionEngine connectionEngine = communityDelegator
                .getDriver()
                .getConnectionEngine(coreDelegator.getCoreProperties(), communityDelegator.getCommunityProperties());
        connectionEngine.initialize();
        return RunnerCreator.create(
                new CommunityFactory(connectionEngine),
                coreDelegator.getCoreProperties(),
                communityDelegator.getCommunityProperties(),
                eventPublisher,
                new SpringDependencyContext(getSpringContext()),
                getCoreProperties().isThrowExceptionIfCannotObtainLock()
        );

    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CoreProperties getCoreProperties() {
        return coreDelegator.getCoreProperties();
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreDelegator.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreDelegator.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreDelegator.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreDelegator.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return coreDelegator.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return coreDelegator.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return coreDelegator.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return coreDelegator.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreDelegator.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return coreDelegator.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreDelegator.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreDelegator.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultAuthor(String publicMigrationAuthor) {
        return coreDelegator.setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreDelegator.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreDelegator.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreDelegator.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreDelegator.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreDelegator.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreDelegator.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreDelegator.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreDelegator.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreDelegator.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreDelegator.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreDelegator.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreDelegator.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreDelegator.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreDelegator.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreDelegator.getTransactionStrategy();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  COMMUNITY
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunitySpringbootBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return communityDelegator.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return communityDelegator.getDriver();
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return communityDelegator.getMigrationScanPackage();
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityDelegator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityDelegator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunitySpringbootBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityDelegator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityDelegator.getMigrationRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setMigrationRepositoryName(String value) {
        return communityDelegator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityDelegator.getLockRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setLockRepositoryName(String value) {
        return communityDelegator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityDelegator.isIndexCreation();
    }

    @Override
    public CommunitySpringbootBuilder setIndexCreation(boolean value) {
        return communityDelegator.setIndexCreation(value);
    }

    @Override
    public CommunityProperties getCommunityProperties() {
        return communityDelegator.getCommunityProperties();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  SPRINGBOOT
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return springbootDelegator.setSpringContext(springContext);
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springbootDelegator.getSpringContext();
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return springbootDelegator.setEventPublisher(applicationEventPublisher);
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return springbootDelegator.getEventPublisher();
    }

    @Override
    public CommunitySpringbootBuilder setRunnerType(SpringRunnerType runnerType) {
        return springbootDelegator.setRunnerType(runnerType);
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootDelegator.getRunnerType();
    }
}
