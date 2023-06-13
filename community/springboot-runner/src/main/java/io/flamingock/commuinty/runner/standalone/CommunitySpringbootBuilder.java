package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.MongockConfiguration;
import io.flamingock.community.internal.MongockFactory;
import io.flamingock.community.internal.MongockRunnerConfigurator;
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
        MongockRunnerConfigurator<CommunitySpringbootBuilder>,
        CoreSpringbootBuilder<CommunitySpringbootBuilder>,
        Configurator<CommunitySpringbootBuilder, MongockConfiguration> {

    private final CoreSpringbootBuilderImpl<
            CommunitySpringbootBuilder,
                    SingleAuditProcessStatus,
                    SingleExecutableProcess,
                    MongockConfiguration> delegate;

    private ConnectionDriver<?> connectionDriver;

    CommunitySpringbootBuilder() {
        this(new MongockConfiguration());
    }

    CommunitySpringbootBuilder(MongockConfiguration configuration) {
        this.delegate = new CoreSpringbootBuilderImpl<>(configuration, () -> this);
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

    @Override
    public CommunitySpringbootBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return null;
    }

    @Override
    public CommunitySpringbootBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return null;
    }

    @Override
    public String getMigrationRepositoryName() {
        return null;
    }

    @Override
    public CommunitySpringbootBuilder setMigrationRepositoryName(String value) {
        return null;
    }

    @Override
    public String getLockRepositoryName() {
        return null;
    }

    @Override
    public CommunitySpringbootBuilder setLockRepositoryName(String value) {
        return null;
    }

    @Override
    public boolean isIndexCreation() {
        return false;
    }

    @Override
    public CommunitySpringbootBuilder setIndexCreation(boolean value) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public CommunitySpringbootBuilder setConfiguration(MongockConfiguration configuration) {
        return delegate.setConfiguration(configuration);
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return delegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return delegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return delegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return delegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return delegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return delegate.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return delegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return delegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return delegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return delegate.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return delegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return delegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return delegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
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
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return delegate.setSpringContext(springContext);
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return delegate.setEventPublisher(applicationEventPublisher);
    }
}
