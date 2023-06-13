package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityRunnerConfigurator;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.configuration.LegacyMigration;
import io.flamingock.core.core.configuration.TransactionStrategy;
import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.runner.Configurator;
import io.flamingock.core.core.runner.Runner;
import io.flamingock.core.core.runner.RunnerBuilder;
import io.flamingock.core.core.runner.standalone.CoreStandaloneBuilderImpl;
import io.flamingock.core.core.runner.standalone.CoreStandaloneBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommunityStandaloneBuilder
        implements
        RunnerBuilder,
        CommunityRunnerConfigurator<CommunityStandaloneBuilder>,
        CoreStandaloneBuilder<CommunityStandaloneBuilder>,
        Configurator<CommunityStandaloneBuilder, CommunityConfiguration> {

    private final CoreStandaloneBuilderImpl<
            CommunityStandaloneBuilder,
                        SingleAuditProcessStatus,
                        SingleExecutableProcess,
            CommunityConfiguration> standaloneBuilderDelegate;

    private ConnectionDriver<?> connectionDriver;

    CommunityStandaloneBuilder() {
        this(new CommunityConfiguration());
    }

    CommunityStandaloneBuilder(CommunityConfiguration configuration) {
        this.standaloneBuilderDelegate = new CoreStandaloneBuilderImpl<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = connectionDriver.getConnectionEngine(standaloneBuilderDelegate.getConfiguration());
        connectionEngine.initialize();
        return standaloneBuilderDelegate.build(new CommunityFactory(connectionEngine));
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return standaloneBuilderDelegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> scanPackage) {
        standaloneBuilderDelegate.getConfiguration().setMigrationScanPackage(scanPackage);
        return this;
    }

    @Override
    public CommunityStandaloneBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        if (migrationScanPackageList != null) {
            List<String> migrationScanPackage = getMigrationScanPackage();
            migrationScanPackage.addAll(migrationScanPackageList);
            setMigrationScanPackage(migrationScanPackage);
        }
        return this;
    }

    @Override
    public String getMigrationRepositoryName() {
        return standaloneBuilderDelegate.getConfiguration().getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        standaloneBuilderDelegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return standaloneBuilderDelegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        standaloneBuilderDelegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return standaloneBuilderDelegate.getConfiguration().isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        standaloneBuilderDelegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder setConfiguration(CommunityConfiguration configuration) {
        return standaloneBuilderDelegate.setConfiguration(configuration);
    }

    @Override
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return standaloneBuilderDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return standaloneBuilderDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return standaloneBuilderDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return standaloneBuilderDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return standaloneBuilderDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return standaloneBuilderDelegate.setEnabled(enabled);
    }


    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return standaloneBuilderDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return standaloneBuilderDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return standaloneBuilderDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return standaloneBuilderDelegate.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return standaloneBuilderDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return standaloneBuilderDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return standaloneBuilderDelegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return standaloneBuilderDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return standaloneBuilderDelegate.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return standaloneBuilderDelegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return standaloneBuilderDelegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return standaloneBuilderDelegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return standaloneBuilderDelegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return standaloneBuilderDelegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return standaloneBuilderDelegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return standaloneBuilderDelegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return standaloneBuilderDelegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return standaloneBuilderDelegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return standaloneBuilderDelegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return standaloneBuilderDelegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return standaloneBuilderDelegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return standaloneBuilderDelegate.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return standaloneBuilderDelegate.getTransactionStrategy();
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return standaloneBuilderDelegate.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return standaloneBuilderDelegate.setMigrationStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return standaloneBuilderDelegate.setMigrationSuccessListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return standaloneBuilderDelegate.setMigrationFailureListener(listener);
    }
}
