package io.flamingock.community.internal;

import java.util.List;

public interface CommunityConfigurable {
    List<String> getMigrationScanPackage();

    void setMigrationScanPackage(List<String> migrationScanPackage);

    String getMigrationRepositoryName();

    void setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    void setLockRepositoryName(String value);

    boolean isIndexCreation();

    void setIndexCreation(boolean value);
}
