package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.core.runner.CoreConfigurator;

import java.util.List;

public interface CommunityConfigurator<HOLDER, CONFIG extends CommunityConfiguration>
extends CoreConfigurator<HOLDER, CONFIG> {

    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();

    List<String> getMigrationScanPackage();

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);

    HOLDER addMigrationScanPackages(List<String> migrationScanPackageList);


    HOLDER addMigrationScanPackage(String migrationScanPackage);

    String getMigrationRepositoryName();

    HOLDER setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    HOLDER setLockRepositoryName(String value);

    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);
}
