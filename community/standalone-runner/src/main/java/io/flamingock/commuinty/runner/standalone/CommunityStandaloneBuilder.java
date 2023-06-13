package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityConfigurator;
import io.flamingock.community.internal.CommunityConfiguratorImpl;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runner.CoreConfigurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.core.runner.RunnerCreator;
import io.flamingock.core.core.runner.standalone.DefaultStandaloneConfigurator;
import io.flamingock.core.core.runner.standalone.StandaloneConfigurator;
import io.flamingock.core.core.runtime.dependency.DependencyContext;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommunityStandaloneBuilder
        implements
        CoreConfigurator<CommunityStandaloneBuilder, CommunityConfiguration>,
        CommunityConfigurator<CommunityStandaloneBuilder, CommunityConfiguration>,
        StandaloneConfigurator<CommunityStandaloneBuilder>,
        RunnerBuilder {

    private final DefaultStandaloneConfigurator<CommunityStandaloneBuilder, CommunityConfiguration> standaloneConfigurator;

    private final CommunityConfigurator<CommunityStandaloneBuilder, CommunityConfiguration> communityConfigurator;

    CommunityStandaloneBuilder() {
        this(new CommunityConfiguration());
    }

    CommunityStandaloneBuilder(CommunityConfiguration configuration) {
        this.standaloneConfigurator = new DefaultStandaloneConfigurator<>(configuration, () -> this);
        this.communityConfigurator = new CommunityConfiguratorImpl<>(configuration, () -> this);
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
        ConnectionEngine connectionEngine = communityConfigurator
                .getDriver()
                .getConnectionEngine(standaloneConfigurator.getConfiguration());
        connectionEngine.initialize();
        return RunnerCreator.create(
                new CommunityFactory(connectionEngine),
                getConfiguration(),
                eventPublisher,
                getDependencyContext(),
                getConfiguration().isThrowExceptionIfCannotObtainLock()
        );
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  CoreStandaloneBuilder
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder setConfiguration(CommunityConfiguration configuration) {
        return standaloneConfigurator.setConfiguration(configuration);
    }

    @Override
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return standaloneConfigurator.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return standaloneConfigurator.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return standaloneConfigurator.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return standaloneConfigurator.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return standaloneConfigurator.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return standaloneConfigurator.setEnabled(enabled);
    }

    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return standaloneConfigurator.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return standaloneConfigurator.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return standaloneConfigurator.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return standaloneConfigurator.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return standaloneConfigurator.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return standaloneConfigurator.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return standaloneConfigurator.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return standaloneConfigurator.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return standaloneConfigurator.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return standaloneConfigurator.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return standaloneConfigurator.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return standaloneConfigurator.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return standaloneConfigurator.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return standaloneConfigurator.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return standaloneConfigurator.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return standaloneConfigurator.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return standaloneConfigurator.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return standaloneConfigurator.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return standaloneConfigurator.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return standaloneConfigurator.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return standaloneConfigurator.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return standaloneConfigurator.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return standaloneConfigurator.getTransactionStrategy();
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneConfigurator.getDependencyContext();
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Object instance) {
        return standaloneConfigurator.addDependency(instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Object instance) {
        return standaloneConfigurator.addDependency(name, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Class<?> type, Object instance) {
        return standaloneConfigurator.addDependency(type, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return standaloneConfigurator.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return standaloneConfigurator.setMigrationStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return standaloneConfigurator.setMigrationSuccessListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return standaloneConfigurator.setMigrationFailureListener(listener);
    }

    @Override
    public Consumer<MigrationStartedEvent> getMigrationStartedListener() {
        return standaloneConfigurator.getMigrationStartedListener();
    }

    @Override
    public Consumer<MigrationSuccessEvent> getMigrationSuccessListener() {
        return standaloneConfigurator.getMigrationSuccessListener();
    }

    @Override
    public Consumer<MigrationFailureEvent> getMigrationFailureListener() {
        return standaloneConfigurator.getMigrationFailureListener();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  communityRunnerConfigurator
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
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
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityConfigurator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityConfigurator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityConfigurator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityConfigurator.getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        return communityConfigurator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityConfigurator.getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        return communityConfigurator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityConfigurator.isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        return communityConfigurator.setIndexCreation(value);
    }
}
