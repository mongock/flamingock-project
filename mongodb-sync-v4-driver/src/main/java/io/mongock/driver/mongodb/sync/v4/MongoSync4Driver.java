package io.mongock.driver.mongodb.sync.v4;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.internal.MongoSync4Auditor;

public class MongoSync4Driver {

    private final MongoDatabase database;
    private MongoSync4Auditor mongoDBAuditor;

    public MongoSync4Driver(MongoClient mongoClient, String database) {
        this.database = mongoClient.getDatabase(database);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoSync4Auditor getMongoDBAuditor() {
        return mongoDBAuditor;
    }
}
