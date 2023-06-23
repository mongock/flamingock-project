package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.List;

public interface CommunityConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    List<String> getMigrationScanPackage();

    HOLDER addMigrationScanPackages(List<String> migrationScanPackageList);

    HOLDER addMigrationScanPackage(String migrationScanPackage);
    
    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);
    
    String getMigrationRepositoryName();

    HOLDER setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    HOLDER setLockRepositoryName(String value);

    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);

    CommunityConfiguration getCommunityProperties();
}
