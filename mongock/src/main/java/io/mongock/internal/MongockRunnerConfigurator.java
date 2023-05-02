package io.mongock.internal;

import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.runner.standalone.MongockStandaloneRunnerBuilder;

public interface MongockRunnerConfigurator {

    void setConnectionDriver(ConnectionDriver<?> connectionDriver);

    String getScanPackage();

    MongockStandaloneRunnerBuilder setScanPackage(String scanPackage);

    String getMigrationRepositoryName();

    MongockStandaloneRunnerBuilder setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    MongockStandaloneRunnerBuilder setLockRepositoryName(String value);

    boolean isIndexCreation();

    MongockStandaloneRunnerBuilder setIndexCreation(boolean value);
}
