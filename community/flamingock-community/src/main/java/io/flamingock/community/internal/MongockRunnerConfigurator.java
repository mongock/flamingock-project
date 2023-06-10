package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.Collections;
import java.util.List;

public interface MongockRunnerConfigurator<HOLDER> {

    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    List<String> getMigrationScanPackage();

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);

    HOLDER addMigrationScanPackages(List<String> migrationScanPackageList);


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
