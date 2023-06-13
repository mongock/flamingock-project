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
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runner.CoreConfigurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.core.runner.standalone.CoreStandaloneBuilderImpl;
import io.flamingock.core.core.runner.standalone.CoreStandaloneConfigurator;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommunityStandaloneBuilder
        implements
        CoreConfigurator<CommunityStandaloneBuilder, CommunityConfiguration>,
        CommunityConfigurator<CommunityStandaloneBuilder, CommunityConfiguration>,
        CoreStandaloneConfigurator<CommunityStandaloneBuilder>,
        RunnerBuilder {

    private final CoreStandaloneBuilderImpl<
            CommunityStandaloneBuilder,
            SingleAuditProcessStatus,
            SingleExecutableProcess,
            CommunityConfiguration> coreStandaloneBuilderDelegate;

    private final CommunityConfigurator<CommunityStandaloneBuilder, CommunityConfiguration> communityRunnerConfigurator;

    CommunityStandaloneBuilder() {
        this(new CommunityConfiguration());
    }

    CommunityStandaloneBuilder(CommunityConfiguration configuration) {
        this.coreStandaloneBuilderDelegate = new CoreStandaloneBuilderImpl<>(configuration, () -> this);
        this.communityRunnerConfigurator = new CommunityConfiguratorImpl<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = communityRunnerConfigurator
                .getDriver()
                .getConnectionEngine(coreStandaloneBuilderDelegate.getConfiguration());
        connectionEngine.initialize();
        return coreStandaloneBuilderDelegate.build(new CommunityFactory(connectionEngine));
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  CoreStandaloneBuilder
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder setConfiguration(CommunityConfiguration configuration) {
        return coreStandaloneBuilderDelegate.setConfiguration(configuration);
    }

    @Override
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreStandaloneBuilderDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreStandaloneBuilderDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreStandaloneBuilderDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreStandaloneBuilderDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return coreStandaloneBuilderDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return coreStandaloneBuilderDelegate.setEnabled(enabled);
    }

    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return coreStandaloneBuilderDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return coreStandaloneBuilderDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreStandaloneBuilderDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return coreStandaloneBuilderDelegate.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreStandaloneBuilderDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreStandaloneBuilderDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return coreStandaloneBuilderDelegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreStandaloneBuilderDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return coreStandaloneBuilderDelegate.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreStandaloneBuilderDelegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreStandaloneBuilderDelegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreStandaloneBuilderDelegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreStandaloneBuilderDelegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreStandaloneBuilderDelegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreStandaloneBuilderDelegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreStandaloneBuilderDelegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreStandaloneBuilderDelegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreStandaloneBuilderDelegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreStandaloneBuilderDelegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreStandaloneBuilderDelegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreStandaloneBuilderDelegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return coreStandaloneBuilderDelegate.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreStandaloneBuilderDelegate.getTransactionStrategy();
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Object instance) {
        return coreStandaloneBuilderDelegate.addDependency(instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Object instance) {
        return coreStandaloneBuilderDelegate.addDependency(name, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Class<?> type, Object instance) {
        return coreStandaloneBuilderDelegate.addDependency(type, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return coreStandaloneBuilderDelegate.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return coreStandaloneBuilderDelegate.setMigrationStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return coreStandaloneBuilderDelegate.setMigrationSuccessListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return coreStandaloneBuilderDelegate.setMigrationFailureListener(listener);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  communityRunnerConfigurator
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return communityRunnerConfigurator.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return communityRunnerConfigurator.getDriver();
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return communityRunnerConfigurator.getMigrationScanPackage();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityRunnerConfigurator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityRunnerConfigurator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityRunnerConfigurator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityRunnerConfigurator.getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        return communityRunnerConfigurator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityRunnerConfigurator.getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        return communityRunnerConfigurator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityRunnerConfigurator.isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        return communityRunnerConfigurator.setIndexCreation(value);
    }
}
