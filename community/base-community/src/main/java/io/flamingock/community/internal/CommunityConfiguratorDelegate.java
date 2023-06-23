package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommunityConfiguratorDelegate<HOLDER> implements CommunityConfigurator<HOLDER> {

    private final CommunityConfiguration communityConfiguration;
    private final Supplier<HOLDER> holderSupplier;
    private ConnectionDriver<?> connectionDriver;

    public CommunityConfiguratorDelegate(CommunityConfiguration communityConfiguration, Supplier<HOLDER> holderSupplier) {
        this.communityConfiguration = communityConfiguration;
        this.holderSupplier = holderSupplier;

    }

    @Override
    public HOLDER setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return holderSupplier.get();
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
        return holderSupplier.get();
    }

    @Override
    public String getMigrationRepositoryName() {
        return communityConfiguration.getMigrationRepositoryName();
    }

    @Override
    public HOLDER setMigrationRepositoryName(String value) {
        communityConfiguration.setMigrationRepositoryName(value);
        return holderSupplier.get();
    }

    @Override
    public String getLockRepositoryName() {
        return communityConfiguration.getLockRepositoryName();
    }

    @Override
    public HOLDER setLockRepositoryName(String value) {
        return holderSupplier.get();
    }

    @Override
    public boolean isIndexCreation() {
        return communityConfiguration.isIndexCreation();
    }

    @Override
    public HOLDER setIndexCreation(boolean value) {
        communityConfiguration.setIndexCreation(value);
        return holderSupplier.get();
    }

    @Override
    public CommunityConfiguration getCommunityProperties() {
        return communityConfiguration;
    }
}
