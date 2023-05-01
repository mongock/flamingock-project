package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockLockProvider;
import io.mongock.internal.driver.ConnectionEngine;
import io.mongock.internal.driver.MongockAuditor;

public class MongoSync4Engine implements ConnectionEngine<MongoDBSync4Configuration> {

    private final MongoDatabase database;

    private MongoSync4Auditor auditor;
    private MongoSync4LockProvider lockProvider;
    private MongoDBSync4Configuration driverConfiguration;
    private MongockConfiguration mongockConfiguration;

    public MongoSync4Engine(MongoDatabase database) {
        this.database = database;
    }

    public void initialize() {
        auditor = new MongoSync4Auditor(database, mongockConfiguration.getLockRepositoryName(), driverConfiguration.getReadWriteConfiguration());
        auditor.initialize(mongockConfiguration.isIndexCreation());
        lockProvider = new MongoSync4LockProvider(database, mongockConfiguration.getLockRepositoryName());
    }

    @Override
    public ConnectionEngine<MongoDBSync4Configuration> setDriverConfiguration(MongoDBSync4Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine<MongoDBSync4Configuration> setMongockConfiguration(MongockConfiguration mongockConfiguration) {
        this.mongockConfiguration = mongockConfiguration;
        return this;
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
