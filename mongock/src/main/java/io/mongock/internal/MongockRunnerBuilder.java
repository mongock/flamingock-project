package io.mongock.internal;

import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.configuration.LegacyMigration;
import io.mongock.core.configuration.TransactionStrategy;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.runner.BaseRunnerBuilder;
import io.mongock.core.runner.Runner;
import io.mongock.core.runner.RunnerBuilder;
import io.mongock.internal.driver.ConnectionEngine;

import java.util.List;
import java.util.Map;

public class MongockRunnerBuilder implements RunnerBuilder<MongockRunnerBuilder, MongockConfiguration> {

    private final BaseRunnerBuilder<
            MongockRunnerBuilder,
            SingleAuditProcessStatus,
            SingleExecutableProcess,
            MongockConfiguration> baseBuilder;

    private ConnectionEngine connectionDriver;

    public MongockRunnerBuilder() {
        this(new MongockConfiguration());
    }

    MongockRunnerBuilder(MongockConfiguration configuration) {
        this.baseBuilder = new BaseRunnerBuilder<>(configuration, () -> this);
    }

    public void setConnectionDriver(ConnectionEngine connectionDriver) {
        this.connectionDriver = connectionDriver;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  MONGOCK CONFIGURATION
    ///////////////////////////////////////////////////////////////////////////////////
    public String getScanPackage() {
        return baseBuilder.getConfiguration().getScanPackage();
    }

    public MongockRunnerBuilder setScanPackage(String scanPackage) {
        baseBuilder.getConfiguration().setScanPackage(scanPackage);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BASE
    ///////////////////////////////////////////////////////////////////////////////////

    public Runner build() {
        baseBuilder.setFactory(new MongockFactory(connectionDriver));
        return baseBuilder.build();
    }

    public MongockRunnerBuilder setConfiguration(MongockConfiguration configuration) {
        return baseBuilder.setConfiguration(configuration);
    }

    public MongockRunnerBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return baseBuilder.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    public MongockRunnerBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return baseBuilder.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    public MongockRunnerBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return baseBuilder.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    public MongockRunnerBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return baseBuilder.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    public MongockRunnerBuilder setTrackIgnored(boolean trackIgnored) {
        return baseBuilder.setTrackIgnored(trackIgnored);
    }

    public MongockRunnerBuilder setEnabled(boolean enabled) {
        return baseBuilder.setEnabled(enabled);
    }

    public MongockRunnerBuilder setMigrationScanPackage(List<String> migrationScanPackage) {
        return baseBuilder.setMigrationScanPackage(migrationScanPackage);
    }

    public MongockRunnerBuilder setStartSystemVersion(String startSystemVersion) {
        return baseBuilder.setStartSystemVersion(startSystemVersion);
    }

    public MongockRunnerBuilder setEndSystemVersion(String endSystemVersion) {
        return baseBuilder.setEndSystemVersion(endSystemVersion);
    }

    public MongockRunnerBuilder setServiceIdentifier(String serviceIdentifier) {
        return baseBuilder.setServiceIdentifier(serviceIdentifier);
    }

    public MongockRunnerBuilder setMetadata(Map<String, Object> metadata) {
        return baseBuilder.setMetadata(metadata);
    }

    public MongockRunnerBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return baseBuilder.setLegacyMigration(legacyMigration);
    }

    public MongockRunnerBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return baseBuilder.setTransactionEnabled(transactionEnabled);
    }

    public MongockRunnerBuilder setDefaultMigrationAuthor(String defaultMigrationAuthor) {
        return baseBuilder.setDefaultMigrationAuthor(defaultMigrationAuthor);
    }

    public MongockRunnerBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return baseBuilder.setTransactionStrategy(transactionStrategy);
    }

    public MongockConfiguration getConfiguration() {
        return baseBuilder.getConfiguration();
    }

    public long getLockAcquiredForMillis() {
        return baseBuilder.getLockAcquiredForMillis();
    }

    public Long getLockQuitTryingAfterMillis() {
        return baseBuilder.getLockQuitTryingAfterMillis();
    }

    public long getLockTryFrequencyMillis() {
        return baseBuilder.getLockTryFrequencyMillis();
    }

    public boolean isThrowExceptionIfCannotObtainLock() {
        return baseBuilder.isThrowExceptionIfCannotObtainLock();
    }

    public boolean isTrackIgnored() {
        return baseBuilder.isTrackIgnored();
    }

    public boolean isEnabled() {
        return baseBuilder.isEnabled();
    }

    public List<String> getMigrationScanPackage() {
        return baseBuilder.getMigrationScanPackage();
    }

    public String getStartSystemVersion() {
        return baseBuilder.getStartSystemVersion();
    }

    public String getEndSystemVersion() {
        return baseBuilder.getEndSystemVersion();
    }

    public String getServiceIdentifier() {
        return baseBuilder.getServiceIdentifier();
    }

    public Map<String, Object> getMetadata() {
        return baseBuilder.getMetadata();
    }

    public LegacyMigration getLegacyMigration() {
        return baseBuilder.getLegacyMigration();
    }

    public Boolean getTransactionEnabled() {
        return baseBuilder.getTransactionEnabled();
    }

    public String getDefaultMigrationAuthor() {
        return baseBuilder.getDefaultMigrationAuthor();
    }

    public TransactionStrategy getTransactionStrategy() {
        return baseBuilder.getTransactionStrategy();
    }
}
