package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.community.internal.DriverConfigurable;

public class MongoDBDriverConfiguration implements DriverConfigurable {

    public final static String LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME = "mongockChangeLog";
    public final static String LEGACY_DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";


    private String migrationRepositoryName = LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
    private String lockRepositoryName = LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;


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
}
