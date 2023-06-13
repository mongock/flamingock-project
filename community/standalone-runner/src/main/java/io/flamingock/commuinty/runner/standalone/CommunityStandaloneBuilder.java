package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.MongockConfiguration;
import io.flamingock.community.internal.MongockFactory;
import io.flamingock.community.internal.MongockRunnerConfigurator;
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
        MongockRunnerConfigurator<CommunityStandaloneBuilder>,
        CoreStandaloneBuilder<CommunityStandaloneBuilder>,
        Configurator<CommunityStandaloneBuilder, MongockConfiguration> {

    private final CoreStandaloneBuilderImpl<
            CommunityStandaloneBuilder,
                        SingleAuditProcessStatus,
                        SingleExecutableProcess,
                        MongockConfiguration> delegate;

    private ConnectionDriver<?> connectionDriver;

    CommunityStandaloneBuilder() {
        this(new MongockConfiguration());
    }

    CommunityStandaloneBuilder(MongockConfiguration configuration) {
        this.delegate = new CoreStandaloneBuilderImpl<>(configuration, () -> this);
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
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return delegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationScanPackage(List<String> scanPackage) {
        delegate.getConfiguration().setMigrationScanPackage(scanPackage);
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
        return delegate.getConfiguration().getMigrationRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setMigrationRepositoryName(String value) {
        delegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return delegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public CommunityStandaloneBuilder setLockRepositoryName(String value) {
        delegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return delegate.getConfiguration().isIndexCreation();
    }

    @Override
    public CommunityStandaloneBuilder setIndexCreation(boolean value) {
        delegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder setConfiguration(MongockConfiguration configuration) {
        return delegate.setConfiguration(configuration);
    }

    @Override
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return delegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return delegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return delegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return delegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return delegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return delegate.setEnabled(enabled);
    }


    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return delegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return delegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return delegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return delegate.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return delegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return delegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return delegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
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
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return delegate.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
        return delegate.setMigrationStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
        return delegate.setMigrationSuccessListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
        return delegate.setMigrationFailureListener(listener);
    }
}
