package io.flamingock.community.internal;

public interface CommunityConfigurable {

    String getMigrationRepositoryName();

    void setMigrationRepositoryName(String value);

    String getLockRepositoryName();

    void setLockRepositoryName(String value);

    boolean isIndexCreation();

    void setIndexCreation(boolean value);
}
