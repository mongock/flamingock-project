package io.flamingock.commuinty.runner.standalone;

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
import io.flamingock.core.core.configurator.standalone.StandaloneConfigurator;
import io.flamingock.core.core.configurator.standalone.StandaloneDelegator;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommunityStandaloneBuilder
        implements
        CoreConfigurator<CommunityStandaloneBuilder>,
        CommunityConfigurator<CommunityStandaloneBuilder>,
        StandaloneConfigurator<CommunityStandaloneBuilder>,
        RunnerBuilder {

    private final CoreDelegator<CommunityStandaloneBuilder> coreDelegator;

    private final CommunityDelegator<CommunityStandaloneBuilder> communityDelegator;

    private final StandaloneDelegator<CommunityStandaloneBuilder> standaloneDelegator;


    CommunityStandaloneBuilder(CoreProperties coreProperties,
                               CommunityProperties communityProperties,
                               DependencyInjectableContext dependencyInjectableContext) {
        this.coreDelegator = new CoreDelegator<>(coreProperties, () -> this);
        this.communityDelegator = new CommunityDelegator<>(communityProperties, () -> this);
        this.standaloneDelegator = new StandaloneDelegator<>(dependencyInjectableContext, () -> this);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        EventPublisher eventPublisher = new EventPublisher(
                getMigrationStartedListener() != null ? () -> getMigrationStartedListener().accept(new MigrationStartedEvent()) : null,
                getMigrationSuccessListener() != null ? result -> getMigrationSuccessListener().accept(new MigrationSuccessEvent(result)) : null,
                getMigrationFailureListener() != null ? result -> getMigrationFailureListener().accept(new MigrationFailureEvent(result)) : null);
        ConnectionEngine connectionEngine = communityDelegator
                .getDriver()
                .getConnectionEngine(coreDelegator.getCoreProperties(), communityDelegator.getCommunityProperties());
        connectionEngine.initialize();
        return RunnerCreator.create(
                new CommunityFactory(connectionEngine),
                coreDelegator.getCoreProperties(),
                communityDelegator.getCommunityProperties(),
                eventPublisher,
                getDependencyContext(),
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
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreDelegator.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreDelegator.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreDelegator.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreDelegator.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return coreDelegator.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return coreDelegator.setEnabled(enabled);
    }

    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return coreDelegator.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return coreDelegator.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreDelegator.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return coreDelegator.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreDelegator.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreDelegator.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultAuthor(String publicMigrationAuthor) {
        return coreDelegator.setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
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
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
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
    public CommunityStandaloneBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityDelegator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityDelegator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityDelegator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityDelegator.getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        return communityDelegator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityDelegator.getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        return communityDelegator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityDelegator.isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        return communityDelegator.setIndexCreation(value);
    }

    @Override
    public CommunityProperties getCommunityProperties() {
        return communityDelegator.getCommunityProperties();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  STANDALONE
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder addDependency(Object instance) {
        return standaloneDelegator.addDependency(instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Object instance) {
        return standaloneDelegator.addDependency(name, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Class<?> type, Object instance) {
        return standaloneDelegator.addDependency(type, instance);
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneDelegator.getDependencyContext();
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return standaloneDelegator.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return standaloneDelegator.setMigrationStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return standaloneDelegator.setMigrationSuccessListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return standaloneDelegator.setMigrationFailureListener(listener);
    }

    @Override
    public Consumer<MigrationStartedEvent> getMigrationStartedListener() {
        return standaloneDelegator.getMigrationStartedListener();
    }

    @Override
    public Consumer<MigrationSuccessEvent> getMigrationSuccessListener() {
        return standaloneDelegator.getMigrationSuccessListener();
    }

    @Override
    public Consumer<MigrationFailureEvent> getMigrationFailureListener() {
        return standaloneDelegator.getMigrationFailureListener();
    }
}
