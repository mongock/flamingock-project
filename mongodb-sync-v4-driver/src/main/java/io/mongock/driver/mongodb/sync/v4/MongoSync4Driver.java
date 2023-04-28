package io.mongock.driver.mongodb.sync.v4;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.internal.MongoSync4Engine;
import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.internal.driver.ConnectionEngine;

public class MongoSync4Driver implements ConnectionDriver {

    private final MongoDatabase database;

    public MongoSync4Driver(MongoClient mongoClient, String database) {
        this.database = mongoClient.getDatabase(database);
    }

    @Override
    public MongoSync4Engine getConnectionEngine() {
        return new MongoSync4Engine(database);
    }
}
