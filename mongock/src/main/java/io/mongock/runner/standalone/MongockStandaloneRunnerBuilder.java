package io.mongock.runner.standalone;

import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.configuration.LegacyMigration;
import io.mongock.core.configuration.TransactionStrategy;
import io.mongock.core.event.MigrationFailureEvent;
import io.mongock.core.event.MigrationStartedEvent;
import io.mongock.core.event.MigrationSuccessEvent;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runner.Runner;
import io.mongock.core.runner.RunnerBuilder;
import io.mongock.core.runner.RunnerConfigurator;
import io.mongock.core.runner.standalone.BaseStandaloneRunnerBuilder;
import io.mongock.core.runner.standalone.StandaloneRunnerConfigurator;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.runtime.dependency.DependencyManager;
import io.mongock.core.runtime.dependency.DependencyManagerImpl;
import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockFactory;
import io.mongock.internal.MongockRunnerConfigurator;
import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.internal.driver.ConnectionEngine;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MongockStandaloneRunnerBuilder
        implements
        RunnerBuilder,
        MongockRunnerConfigurator<MongockStandaloneRunnerBuilder>,
        StandaloneRunnerConfigurator<MongockStandaloneRunnerBuilder>,
        RunnerConfigurator<MongockStandaloneRunnerBuilder, MongockConfiguration> {

    private final BaseStandaloneRunnerBuilder<
            MongockStandaloneRunnerBuilder,
            SingleAuditProcessStatus,
            SingleExecutableProcess,
            MongockConfiguration> delegate;

    private ConnectionDriver<?> connectionDriver;

    MongockStandaloneRunnerBuilder() {
        this(new MongockConfiguration());
    }

    MongockStandaloneRunnerBuilder(MongockConfiguration configuration) {
        this.delegate = new BaseStandaloneRunnerBuilder<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = connectionDriver.getConnectionEngine(delegate.getConfiguration());
        connectionEngine.initialize();
        DependencyManager dependencyManager = new DependencyManagerImpl();//TODO implement this
        return delegate.build(
                new MongockFactory(connectionEngine),
                new RuntimeHelper.DefaultLockableBuilder(dependencyManager)
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public MongockStandaloneRunnerBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return delegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public MongockStandaloneRunnerBuilder setMigrationScanPackage(List<String> scanPackage) {
        delegate.getConfiguration().setMigrationScanPackage(scanPackage);
        return this;
    }

    @Override
    public MongockStandaloneRunnerBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
        if (migrationScanPackageList != null) {
            List<String> migrationScanPackage = getMigrationScanPackage();
            migrationScanPackage.addAll(migrationScanPackageList);
            setMigrationScanPackage(migrationScanPackage);
        }
        return this;
    }

    @Override
    public String getMigrationRepositoryName() {
        return delegate.getConfiguration().getMigrationRepositoryName();
    }

    @Override
    public MongockStandaloneRunnerBuilder setMigrationRepositoryName(String value) {
        delegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return delegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public MongockStandaloneRunnerBuilder setLockRepositoryName(String value) {
        delegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return delegate.getConfiguration().isIndexCreation();
    }

    @Override
    public MongockStandaloneRunnerBuilder setIndexCreation(boolean value) {
        delegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public MongockStandaloneRunnerBuilder setConfiguration(MongockConfiguration configuration) {
        return delegate.setConfiguration(configuration);
    }

    @Override
    public MongockStandaloneRunnerBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return delegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public MongockStandaloneRunnerBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return delegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public MongockStandaloneRunnerBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return delegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public MongockStandaloneRunnerBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return delegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public MongockStandaloneRunnerBuilder setTrackIgnored(boolean trackIgnored) {
        return delegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public MongockStandaloneRunnerBuilder setEnabled(boolean enabled) {
        return delegate.setEnabled(enabled);
    }


    @Override
    public MongockStandaloneRunnerBuilder setStartSystemVersion(String startSystemVersion) {
        return delegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public MongockStandaloneRunnerBuilder setEndSystemVersion(String endSystemVersion) {
        return delegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public MongockStandaloneRunnerBuilder setServiceIdentifier(String serviceIdentifier) {
        return delegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public MongockStandaloneRunnerBuilder setMetadata(Map<String, Object> metadata) {
        return delegate.setMetadata(metadata);
    }

    @Override
    public MongockStandaloneRunnerBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return delegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public MongockStandaloneRunnerBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return delegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public MongockStandaloneRunnerBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return delegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public MongockStandaloneRunnerBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return delegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public MongockConfiguration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return delegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return delegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return delegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return delegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return delegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return delegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return delegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return delegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return delegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return delegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return delegate.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return delegate.getTransactionStrategy();
    }

    @Override
    public MongockStandaloneRunnerBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return delegate.setMigrationStartedListener(listener);
    }

    @Override
    public MongockStandaloneRunnerBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return delegate.setMigrationSuccessListener(listener);
    }

    @Override
    public MongockStandaloneRunnerBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return delegate.setMigrationFailureListener(listener);
    }
}
