package io.flamingock.oss.driver.mongodb.sync.v4.changes.failedWithTransaction;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(clientSession, new Document().append("name", "Jorge"));
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        // Do nothing
    }
}
