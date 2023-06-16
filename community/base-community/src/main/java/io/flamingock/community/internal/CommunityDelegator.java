package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommunityDelegator<HOLDER> implements CommunityConfigurator<HOLDER> {

    private final CommunityProperties communityProperties;
    private final Supplier<HOLDER> holderSupplier;
    private ConnectionDriver<?> connectionDriver;

    public CommunityDelegator(CommunityProperties communityProperties, Supplier<HOLDER> holderSupplier) {
        this.communityProperties = communityProperties;
        this.holderSupplier = holderSupplier;

    }
    public HOLDER setDriver(ConnectionDriver<?> connectionDriver) {
        this.connectionDriver = connectionDriver;
        return holderSupplier.get();
    }

    public ConnectionDriver<?> getDriver() {
        return  connectionDriver;
    }

    public List<String> getMigrationScanPackage() {
        return communityProperties.getMigrationScanPackage();
    }


    public HOLDER addMigrationScanPackages(List<String> migrationScanPackageList) {
        List<String> packagesCurrentlyStored = getMigrationScanPackage();
        if (migrationScanPackageList != null) {
            packagesCurrentlyStored.addAll(migrationScanPackageList);
        }
        return setMigrationScanPackage(packagesCurrentlyStored);
    }


    public HOLDER addMigrationScanPackage(String migrationScanPackage) {
        return this.addMigrationScanPackages(Collections.singletonList(migrationScanPackage));
    }

    public HOLDER setMigrationScanPackage(List<String> migrationScanPackage) {
        communityProperties.setMigrationScanPackage(migrationScanPackage);
        return holderSupplier.get();
    }


    public String getMigrationRepositoryName() {
        return communityProperties.getMigrationRepositoryName();
    }


    public HOLDER setMigrationRepositoryName(String value) {
        communityProperties.setMigrationRepositoryName(value);
        return holderSupplier.get();
    }


    public String getLockRepositoryName() {
        return communityProperties.getLockRepositoryName();
    }


    public HOLDER setLockRepositoryName(String value) {
        return holderSupplier.get();
    }


    public boolean isIndexCreation() {
        return communityProperties.isIndexCreation();
    }


    public HOLDER setIndexCreation(boolean value) {
        communityProperties.setIndexCreation(value);
        return holderSupplier.get();
    }

    @Override
    public CommunityProperties getCommunityProperties() {
        return communityProperties;
    }
}
