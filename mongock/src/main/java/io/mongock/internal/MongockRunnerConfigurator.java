package io.mongock.internal;

import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.runner.standalone.MongockStandaloneRunnerBuilder;

public interface MongockRunnerConfigurator<HOLDER> {

    void setConnectionDriver(ConnectionDriver<?> connectionDriver);

    String getScanPackage();

    HOLDER setScanPackage(String scanPackage);

    String getMigrationRepositoryName();

    HOLDER setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    MongockStandaloneRunnerBuilder setLockRepositoryName(String value);

    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);
}
