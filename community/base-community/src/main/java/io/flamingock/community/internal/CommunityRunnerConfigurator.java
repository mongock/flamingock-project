package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.core.configuration.CoreConfiguration;

import java.util.Collections;
import java.util.List;

public interface CommunityRunnerConfigurator<HOLDER, CONFIG extends CoreConfiguration> {

    HOLDER setConfiguration(CONFIG config);

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
