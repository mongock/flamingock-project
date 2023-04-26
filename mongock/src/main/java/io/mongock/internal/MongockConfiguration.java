package io.mongock.internal;

import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.internal.driver.MongockDriverConfiguration;

public class MongockConfiguration extends AbstractConfiguration implements MongockDriverConfiguration {

    private String scanPackage;
    private String migrationRepositoryName;
    private String lockRepositoryName;
    private boolean indexCreation;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    @Override
    public String getMigrationRepositoryName() {
        return migrationRepositoryName;
    }

    @Override
    public void setMigrationRepositoryName(String value) {
        this.migrationRepositoryName = value;
    }

    @Override
    public String getLockRepositoryName() {
        return lockRepositoryName;
    }

    @Override
    public void setLockRepositoryName(String value) {
        this.lockRepositoryName = value;
    }

    @Override
    public boolean isIndexCreation() {
        return indexCreation;
    }

    @Override
    public void setIndexCreation(boolean value) {
        this.indexCreation = value;
    }
}
