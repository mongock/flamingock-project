package io.flamingock.oss.runner.standalone;

import io.flamingock.oss.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.oss.core.configuration.LegacyMigration;
import io.flamingock.oss.core.configuration.TransactionStrategy;
import io.flamingock.oss.core.event.MigrationFailureEvent;
import io.flamingock.oss.core.event.MigrationStartedEvent;
import io.flamingock.oss.core.event.MigrationSuccessEvent;
import io.flamingock.oss.core.process.single.SingleExecutableProcess;
import io.flamingock.oss.core.runner.Configurator;
import io.flamingock.oss.core.runner.Runner;
import io.flamingock.oss.core.runner.RunnerBuilder;
import io.flamingock.oss.core.runner.standalone.BaseStandaloneBuilder;
import io.flamingock.oss.core.runner.standalone.StandaloneBuilder;
import io.flamingock.oss.internal.MongockConfiguration;
import io.flamingock.oss.internal.MongockFactory;
import io.flamingock.oss.internal.driver.ConnectionDriver;
import io.flamingock.oss.internal.driver.ConnectionEngine;
import io.flamingock.oss.internal.MongockRunnerConfigurator;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MongockStandaloneBuilder
        implements
        RunnerBuilder,
        MongockRunnerConfigurator<MongockStandaloneBuilder>,
        StandaloneBuilder<MongockStandaloneBuilder>,
        Configurator<MongockStandaloneBuilder, MongockConfiguration> {

    private final BaseStandaloneBuilder<
            MongockStandaloneBuilder,
                SingleAuditProcessStatus,
                SingleExecutableProcess,
                MongockConfiguration> delegate;

    private ConnectionDriver<?> connectionDriver;

    MongockStandaloneBuilder() {
        this(new MongockConfiguration());
    }

    MongockStandaloneBuilder(MongockConfiguration configuration) {
        this.delegate = new BaseStandaloneBuilder<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = connectionDriver.getConnectionEngine(delegate.getConfiguration());
        connectionEngine.initialize();
        return delegate.build(new MongockFactory(connectionEngine));
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public MongockStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return delegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public MongockStandaloneBuilder setMigrationScanPackage(List<String> scanPackage) {
        delegate.getConfiguration().setMigrationScanPackage(scanPackage);
        return this;
    }

    @Override
    public MongockStandaloneBuilder addMigrationScanPackages(List<String> migrationScanPackageList) {
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
    public MongockStandaloneBuilder setMigrationRepositoryName(String value) {
        delegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return delegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public MongockStandaloneBuilder setLockRepositoryName(String value) {
        delegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return delegate.getConfiguration().isIndexCreation();
    }

    @Override
    public MongockStandaloneBuilder setIndexCreation(boolean value) {
        delegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public MongockStandaloneBuilder setConfiguration(MongockConfiguration configuration) {
        return delegate.setConfiguration(configuration);
    }

    @Override
    public MongockStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return delegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public MongockStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return delegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public MongockStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return delegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public MongockStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return delegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public MongockStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return delegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public MongockStandaloneBuilder setEnabled(boolean enabled) {
        return delegate.setEnabled(enabled);
    }


    @Override
    public MongockStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return delegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public MongockStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return delegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public MongockStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return delegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public MongockStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return delegate.setMetadata(metadata);
    }

    @Override
    public MongockStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return delegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public MongockStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return delegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public MongockStandaloneBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return delegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public MongockStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
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
    public MongockStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return delegate.addDependency(name, type, instance);
    }

    @Override
    public MongockStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return delegate.setMigrationStartedListener(listener);
    }

    @Override
    public MongockStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return delegate.setMigrationSuccessListener(listener);
    }

    @Override
    public MongockStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return delegate.setMigrationFailureListener(listener);
    }
}
