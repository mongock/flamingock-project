package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.configurator.LocalConfigurable;

public class MongoDBDriverConfiguration implements LocalConfigurable {

    public final static String LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME = "mongockChangeLog";
    public final static String LEGACY_DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";


    private String migrationRepositoryName = LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
    private String lockRepositoryName = LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
    private boolean indexCreation = true;


    public String getMigrationRepositoryName() {
        return migrationRepositoryName;
    }

    public void setMigrationRepositoryName(String value) {
        this.migrationRepositoryName = value;
    }

    public String getLockRepositoryName() {
        return lockRepositoryName;
    }

    public void setLockRepositoryName(String value) {
        this.lockRepositoryName = value;
    }

    public boolean isIndexCreation() {
        return indexCreation;
    }

    public void setIndexCreation(boolean value) {
        this.indexCreation = value;
    }
}
