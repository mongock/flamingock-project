package io.mongock.internal;

import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.runner.standalone.MongockStandaloneRunnerBuilder;

import java.util.List;

public interface MongockRunnerConfigurator<HOLDER> {

    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    List<String> getMigrationScanPackage();

    HOLDER setMigrationScanPackage(List<String> migrationScanPackage);


    String getMigrationRepositoryName();

    HOLDER setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    MongockStandaloneRunnerBuilder setLockRepositoryName(String value);

    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);
}
