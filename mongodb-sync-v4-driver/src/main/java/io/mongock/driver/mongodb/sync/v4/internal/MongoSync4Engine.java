package io.mongock.driver.mongodb.sync.v4.internal;

import io.mongock.driver.mongodb.sync.v4.MongoSync4Driver;
import io.mongock.internal.MongockLockProvider;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;
import io.mongock.internal.driver.MongockDriverConfiguration;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoSync4Driver driver;

    private MongoSync4Auditor auditor;
    private MongoSync4LockProvider lockProvider;

    public MongoSync4Engine(MongoSync4Driver driver) {
        this.driver = driver;
    }

    @Override
    public void initialize(String executionId, MongockDriverConfiguration configuration) {
        auditor = new MongoSync4Auditor(driver.getDatabase(), configuration.getLockRepositoryName());
        lockProvider = new MongoSync4LockProvider(driver.getDatabase(), configuration.getLockRepositoryName());
    }

    @Override
    public MongockAuditor getAuditor() {
        return auditor;
    }

    @Override
    public MongockLockProvider getLockProvider() {
        return lockProvider;
    }

}
