package io.mongock.driver.mongodb.sync.v4;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.internal.MongoSync4Engine;
import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.internal.driver.ConnectionEngine;

public class MongoSync4Driver implements ConnectionDriver<MongoDBSync4Configuration> {

    private final MongoDatabase database;
    private MongoDBSync4Configuration driverConfiguration;

    public MongoSync4Driver(MongoClient mongoClient, String database) {
        this.database = mongoClient.getDatabase(database);
    }

    @Override
    public MongoSync4Driver setDriverConfiguration(MongoDBSync4Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(MongockConfiguration mongockConfiguration) {
        return new MongoSync4Engine(
                database,
                mongockConfiguration,
                driverConfiguration != null ? driverConfiguration : MongoDBSync4Configuration.getDefault());
    }
}
