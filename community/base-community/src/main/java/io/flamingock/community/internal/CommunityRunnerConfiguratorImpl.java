package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.core.configuration.CoreConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommunityRunnerConfiguratorImpl<HOLDER, CONFIG extends CommunityConfiguration>
        implements CommunityRunnerConfigurator<HOLDER, CONFIG> {

    private final Supplier<HOLDER> holderInstanceSupplier;
    private CONFIG communityConfiguration;

    private ConnectionDriver<?> connectionDriver;

    public CommunityRunnerConfiguratorImpl(CONFIG communityConfiguration,
                                           Supplier<HOLDER> holderInstanceSupplier) {
        this.communityConfiguration = communityConfiguration;
        this.holderInstanceSupplier = holderInstanceSupplier;
    }

    @Override
    public HOLDER setConfiguration(CONFIG communityConfig) {
        this.communityConfiguration = communityConfig;
        return holderInstanceSupplier.get();
    }

    @Override
    public HOLDER setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return holderInstanceSupplier.get();
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return connectionDriver;
    }

    @Override
    public List<String> getMigrationScanPackage() {
        return communityConfiguration.getMigrationScanPackage();
    }

    @Override
    public HOLDER addMigrationScanPackages(List<String> migrationScanPackageList) {
        List<String> packagesCurrentlyStored = getMigrationScanPackage();
        if (migrationScanPackageList != null) {
            packagesCurrentlyStored.addAll(migrationScanPackageList);
        }
        return setMigrationScanPackage(packagesCurrentlyStored);
    }

    @Override
    public HOLDER addMigrationScanPackage(String migrationScanPackage) {
        return this.addMigrationScanPackages(Collections.singletonList(migrationScanPackage));
    }

    @Override
    public HOLDER setMigrationScanPackage(List<String> migrationScanPackage) {
        communityConfiguration.setMigrationScanPackage(migrationScanPackage);
        return holderInstanceSupplier.get();
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityConfiguration.getMigrationRepositoryName();
    }

    @Override
    public HOLDER setMigrationRepositoryName(String value) {
        communityConfiguration.setMigrationRepositoryName(value);
        return holderInstanceSupplier.get();
    }

    @Override
    public String getLockRepositoryName() {
        return communityConfiguration.getLockRepositoryName();
    }

    @Override
    public HOLDER setLockRepositoryName(String value) {
        return holderInstanceSupplier.get();
    }

    @Override
    public boolean isIndexCreation() {
        return communityConfiguration.isIndexCreation();
    }

    @Override
    public HOLDER setIndexCreation(boolean value) {
        communityConfiguration.setIndexCreation(value);
        return holderInstanceSupplier.get();
    }
}
