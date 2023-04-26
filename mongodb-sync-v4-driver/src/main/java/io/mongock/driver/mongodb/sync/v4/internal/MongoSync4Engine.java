package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.MongoSync4Driver;
import io.mongock.internal.MongockLockProvider;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;
import io.mongock.internal.driver.MongockDriverConfiguration;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoDatabase database;

    private MongoSync4Auditor auditor;
    private MongoSync4LockProvider lockProvider;

    public MongoSync4Engine(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void initialize(MongockDriverConfiguration configuration) {
        auditor = new MongoSync4Auditor(database, configuration.getLockRepositoryName());
        lockProvider = new MongoSync4LockProvider(database, configuration.getLockRepositoryName());
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
