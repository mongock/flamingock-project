package io.flamingock.community.internal;

import static io.flamingock.core.util.Constants.LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
import static io.flamingock.core.util.Constants.LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;

public class CommunityConfiguration implements CommunityConfigurable {

    private String migrationRepositoryName = LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
    private String lockRepositoryName = LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
    private boolean indexCreation = true;

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
