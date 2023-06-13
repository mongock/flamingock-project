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
        CommunityRunnerConfigurator<CommunityStandaloneBuilder, CommunityConfiguration>,
        CoreStandaloneBuilder<CommunityStandaloneBuilder>,
        Configurator<CommunityStandaloneBuilder, CommunityConfiguration> {

    private final CoreStandaloneBuilderImpl<
            CommunityStandaloneBuilder,
                        SingleAuditProcessStatus,
                        SingleExecutableProcess,
            CommunityConfiguration> coreStandaloneBuilderDelegate;

    private ConnectionDriver<?> connectionDriver;

    CommunityStandaloneBuilder() {
        this(new CommunityConfiguration());
    }

    CommunityStandaloneBuilder(CommunityConfiguration configuration) {
        this.coreStandaloneBuilderDelegate = new CoreStandaloneBuilderImpl<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = connectionDriver.getConnectionEngine(coreStandaloneBuilderDelegate.getConfiguration());
        connectionEngine.initialize();
        return coreStandaloneBuilderDelegate.build(new CommunityFactory(connectionEngine));
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
        return coreStandaloneBuilderDelegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> scanPackage) {
        coreStandaloneBuilderDelegate.getConfiguration().setMigrationScanPackage(scanPackage);
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
        return coreStandaloneBuilderDelegate.getConfiguration().getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        coreStandaloneBuilderDelegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return coreStandaloneBuilderDelegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        coreStandaloneBuilderDelegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return coreStandaloneBuilderDelegate.getConfiguration().isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        coreStandaloneBuilderDelegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
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
}
