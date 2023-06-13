package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityRunnerConfigurator;
import io.flamingock.community.internal.CommunityRunnerConfiguratorImpl;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runner.Configurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.spring.builder.CoreSpringbootBuilderImpl;
import io.flamingock.core.spring.builder.CoreSpringbootBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;

public class CommunitySpringbootBuilder
        implements
        RunnerBuilder,
        CommunityRunnerConfigurator<CommunitySpringbootBuilder, CommunityConfiguration>,
        CoreSpringbootBuilder<CommunitySpringbootBuilder>,
        Configurator<CommunitySpringbootBuilder, CommunityConfiguration> {

    private final CoreSpringbootBuilderImpl<
            CommunitySpringbootBuilder,
            SingleAuditProcessStatus,
            SingleExecutableProcess,
            CommunityConfiguration> coreSpringbootBuilderDelegate;

    private final CommunityRunnerConfigurator<CommunitySpringbootBuilder, CommunityConfiguration> communityRunnerConfigurator;


    CommunitySpringbootBuilder() {
        this(new CommunityConfiguration());
    }

    CommunitySpringbootBuilder(CommunityConfiguration configuration) {
        this.coreSpringbootBuilderDelegate = new CoreSpringbootBuilderImpl<>(configuration, () -> this);
        this.communityRunnerConfigurator = new CommunityRunnerConfiguratorImpl<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = communityRunnerConfigurator
                .getDriver()
                .getConnectionEngine(coreSpringbootBuilderDelegate.getConfiguration());
        connectionEngine.initialize();
        return coreSpringbootBuilderDelegate.build(new CommunityFactory(connectionEngine));
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CoreSpringbootBuilder
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunitySpringbootBuilder setConfiguration(CommunityConfiguration configuration) {
        communityRunnerConfigurator.setConfiguration(configuration);
        coreSpringbootBuilderDelegate.setConfiguration(configuration);
        return this;
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreSpringbootBuilderDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreSpringbootBuilderDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreSpringbootBuilderDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreSpringbootBuilderDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return coreSpringbootBuilderDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return coreSpringbootBuilderDelegate.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return coreSpringbootBuilderDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return coreSpringbootBuilderDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreSpringbootBuilderDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return coreSpringbootBuilderDelegate.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreSpringbootBuilderDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreSpringbootBuilderDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return coreSpringbootBuilderDelegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreSpringbootBuilderDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return coreSpringbootBuilderDelegate.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreSpringbootBuilderDelegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreSpringbootBuilderDelegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreSpringbootBuilderDelegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreSpringbootBuilderDelegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreSpringbootBuilderDelegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreSpringbootBuilderDelegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreSpringbootBuilderDelegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreSpringbootBuilderDelegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreSpringbootBuilderDelegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreSpringbootBuilderDelegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreSpringbootBuilderDelegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreSpringbootBuilderDelegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return coreSpringbootBuilderDelegate.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreSpringbootBuilderDelegate.getTransactionStrategy();
    }

    @Override
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return coreSpringbootBuilderDelegate.setSpringContext(springContext);
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return coreSpringbootBuilderDelegate.setEventPublisher(applicationEventPublisher);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  communityRunnerConfigurator
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunitySpringbootBuilder setDriver(ConnectionDriver<?> connectionDriver) {
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
    public CommunitySpringbootBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return communityRunnerConfigurator.setMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        return communityRunnerConfigurator.addMigrationScanPackages(migrationScanPackageList);
    }

    @Override
    public CommunitySpringbootBuilder addMigrationScanPackage(String migrationScanPackage) {
        return communityRunnerConfigurator.addMigrationScanPackage(migrationScanPackage);
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityRunnerConfigurator.getMigrationRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setMigrationRepositoryName(String value) {
        return communityRunnerConfigurator.setMigrationRepositoryName(value);
    }

    @Override
    public String getLockRepositoryName() {
        return communityRunnerConfigurator.getLockRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder setLockRepositoryName(String value) {
        return communityRunnerConfigurator.setLockRepositoryName(value);
    }

    @Override
    public boolean isIndexCreation() {
        return communityRunnerConfigurator.isIndexCreation();
    }

    @Override
    public CommunitySpringbootBuilder setIndexCreation(boolean value) {
        return communityRunnerConfigurator.setIndexCreation(value);
    }
}
