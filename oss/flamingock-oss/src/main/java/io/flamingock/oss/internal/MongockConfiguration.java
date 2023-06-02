package io.flamingock.oss.internal;

import io.flamingock.core.core.configuration.CoreConfiguration;

import java.util.LinkedList;
import java.util.List;

import static io.flamingock.core.core.util.Constants.LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
import static io.flamingock.core.core.util.Constants.LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;

public class MongockConfiguration extends CoreConfiguration {
    private List<String> migrationScanPackage = new LinkedList<>();
    private String migrationRepositoryName = LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
    private String lockRepositoryName = LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
    private boolean indexCreation = true;


    public List<String> getMigrationScanPackage() {
        return migrationScanPackage;
    }

    public void setMigrationScanPackage(List<String> migrationScanPackage) {
        this.migrationScanPackage = migrationScanPackage;
    }

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
