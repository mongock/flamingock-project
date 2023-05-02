package io.mongock.internal;

import io.mongock.core.configuration.AbstractConfiguration;

import java.util.LinkedList;
import java.util.List;

public class MongockConfiguration extends AbstractConfiguration {
private List<String> migrationScanPackage = new LinkedList<>();
    private String migrationRepositoryName;
    private String lockRepositoryName;
    private boolean indexCreation;

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
