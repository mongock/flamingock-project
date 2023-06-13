package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.Collections;
import java.util.List;

public interface CommunityRunnerConfigurator<HOLDER> {

    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    List<String> getMigrationScanPackage();

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);

    default HOLDER addMigrationScanPackages(List<String> migrationScanPackageList) {
        List<String> packagesCurrentlyStored = getMigrationScanPackage();
        if (migrationScanPackageList != null) {
            packagesCurrentlyStored.addAll(migrationScanPackageList);
        }
        return setMigrationScanPackage(packagesCurrentlyStored);
    }


    default HOLDER addMigrationScanPackage(String migrationScanPackage) {
        return this.addMigrationScanPackages(Collections.singletonList(migrationScanPackage));
    }

    String getMigrationRepositoryName();

    HOLDER setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    HOLDER setLockRepositoryName(String value);

    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);
}
