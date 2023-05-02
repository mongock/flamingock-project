package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockLockProvider;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoDatabase database;

    private MongoSync4Auditor auditor;
    private MongoSync4LockProvider lockProvider;
    private final MongoDBSync4Configuration driverConfiguration;
    private final MongockConfiguration mongockConfiguration;

    public MongoSync4Engine(MongoDatabase database,
                            MongockConfiguration mongockConfiguration,
                            MongoDBSync4Configuration driverConfiguration) {
        this.database = database;
        this.driverConfiguration = driverConfiguration;
        this.mongockConfiguration = mongockConfiguration;
    }

    @Override
    public void initialize() {
        auditor = new MongoSync4Auditor(database, mongockConfiguration.getMigrationRepositoryName(), driverConfiguration.getReadWriteConfiguration());
        auditor.initialize(mongockConfiguration.isIndexCreation());
        lockProvider = new MongoSync4LockProvider(database, mongockConfiguration.getLockRepositoryName());
        lockProvider.initialize(mongockConfiguration.isIndexCreation());
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
