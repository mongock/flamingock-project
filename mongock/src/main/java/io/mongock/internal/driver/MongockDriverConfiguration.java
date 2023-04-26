package io.mongock.internal.driver;

public interface MongockDriverConfiguration {


    /**
     * Repository name for changeLogs history
     */
    String getMigrationRepositoryName();
    void setMigrationRepositoryName(String value);
    /**
     * Repository name for locking mechanism
     */
    String getLockRepositoryName();
    void setLockRepositoryName(String value);

    /**
     * If false, Mongock won't create the necessary index. However, it will check that they are already
     * created, failing otherwise. Default true
     */
    boolean isIndexCreation();
    void setIndexCreation(boolean value);

}
