package io.flamingock.commuinty.runner.standalone;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.CommunityFactory;
import io.flamingock.community.internal.CommunityRunnerConfigurator;
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
        CommunityRunnerConfigurator<CommunitySpringbootBuilder>,
        CoreSpringbootBuilder<CommunitySpringbootBuilder>,
        Configurator<CommunitySpringbootBuilder, CommunityConfiguration> {

    private final CoreSpringbootBuilderImpl<
            CommunitySpringbootBuilder,
                    SingleAuditProcessStatus,
                    SingleExecutableProcess,
            CommunityConfiguration> springbootBuilderDelegate;

    private ConnectionDriver<?> connectionDriver;

    CommunitySpringbootBuilder() {
        this(new CommunityConfiguration());
    }

    CommunitySpringbootBuilder(CommunityConfiguration configuration) {
        this.springbootBuilderDelegate = new CoreSpringbootBuilderImpl<>(configuration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = connectionDriver.getConnectionEngine(springbootBuilderDelegate.getConfiguration());
        connectionEngine.initialize();
        return springbootBuilderDelegate.build(new CommunityFactory(connectionEngine));
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
    public CommunitySpringbootBuilder setConfiguration(CommunityConfiguration configuration) {
        return springbootBuilderDelegate.setConfiguration(configuration);
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return springbootBuilderDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return springbootBuilderDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return springbootBuilderDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return springbootBuilderDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return springbootBuilderDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return springbootBuilderDelegate.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return springbootBuilderDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return springbootBuilderDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return springbootBuilderDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return springbootBuilderDelegate.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return springbootBuilderDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return springbootBuilderDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return springbootBuilderDelegate.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return springbootBuilderDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityConfiguration getConfiguration() {
        return springbootBuilderDelegate.getConfiguration();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return springbootBuilderDelegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return springbootBuilderDelegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return springbootBuilderDelegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return springbootBuilderDelegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return springbootBuilderDelegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return springbootBuilderDelegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return springbootBuilderDelegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return springbootBuilderDelegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return springbootBuilderDelegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return springbootBuilderDelegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return springbootBuilderDelegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return springbootBuilderDelegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultMigrationAuthor() {
        return springbootBuilderDelegate.getDefaultMigrationAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return springbootBuilderDelegate.getTransactionStrategy();
    }

    @Override
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return springbootBuilderDelegate.setSpringContext(springContext);
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return springbootBuilderDelegate.setEventPublisher(applicationEventPublisher);
    }
}
