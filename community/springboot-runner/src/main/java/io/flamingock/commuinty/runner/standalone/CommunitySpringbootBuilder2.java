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

public class CommunitySpringbootBuilder2
        implements
        RunnerBuilder,
        MongockRunnerConfigurator<CommunitySpringbootBuilder2>,
        CoreSpringbootBuilder<CommunitySpringbootBuilder2>,
        Configurator<CommunitySpringbootBuilder2, MongockConfiguration> {

    private final CoreSpringbootBuilderImpl<
            CommunitySpringbootBuilder2,
                    SingleAuditProcessStatus,
                    SingleExecutableProcess,
                    MongockConfiguration> delegate;

    private ConnectionDriver<?> connectionDriver;

    CommunitySpringbootBuilder2() {
        this(new MongockConfiguration());
    }

    CommunitySpringbootBuilder2(MongockConfiguration configuration) {
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

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CommunitySpringbootBuilder2 setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return this;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return delegate.getConfiguration().getMigrationScanPackage();
    }

    @Override
    public CommunitySpringbootBuilder2 setMigrationScanPackage(List<String> scanPackage) {
        delegate.getConfiguration().setMigrationScanPackage(scanPackage);
        return this;
    }

    @Override
    public CommunitySpringbootBuilder2 addMigrationScanPackages(List<String> migrationScanPackageList) {
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
    public CommunitySpringbootBuilder2 setMigrationRepositoryName(String value) {
        delegate.getConfiguration().setMigrationRepositoryName(value);
        return this;
    }

    @Override
    public String getLockRepositoryName() {
        return delegate.getConfiguration().getLockRepositoryName();
    }

    @Override
    public CommunitySpringbootBuilder2 setLockRepositoryName(String value) {
        delegate.getConfiguration().setLockRepositoryName(value);
        return this;
    }

    @Override
    public boolean isIndexCreation() {
        return delegate.getConfiguration().isIndexCreation();
    }

    @Override
    public CommunitySpringbootBuilder2 setIndexCreation(boolean value) {
        delegate.getConfiguration().setIndexCreation(value);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE CONFIGURATOR
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunitySpringbootBuilder2 setConfiguration(MongockConfiguration configuration) {
        return delegate.setConfiguration(configuration);
    }

    @Override
    public CommunitySpringbootBuilder2 setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return delegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder2 setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return delegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder2 setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return delegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder2 setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return delegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder2 setTrackIgnored(boolean trackIgnored) {
        return delegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder2 setEnabled(boolean enabled) {
        return delegate.setEnabled(enabled);
    }


    @Override
    public CommunitySpringbootBuilder2 setStartSystemVersion(String startSystemVersion) {
        return delegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder2 setEndSystemVersion(String endSystemVersion) {
        return delegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder2 setServiceIdentifier(String serviceIdentifier) {
        return delegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder2 setMetadata(Map<String, Object> metadata) {
        return delegate.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder2 setLegacyMigration(LegacyMigration legacyMigration) {
        return delegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder2 setTransactionEnabled(Boolean transactionEnabled) {
        return delegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder2 setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return delegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder2 setTransactionStrategy(TransactionStrategy transactionStrategy) {
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
    public CommunitySpringbootBuilder2 setSpringContext(ApplicationContext springContext) {
        return null;
    }

    @Override
    public CommunitySpringbootBuilder2 setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return null;
    }
}
