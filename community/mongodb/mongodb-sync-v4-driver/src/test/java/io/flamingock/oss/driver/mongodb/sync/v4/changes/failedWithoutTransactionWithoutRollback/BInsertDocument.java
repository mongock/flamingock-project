package io.flamingock.oss.driver.mongodb.sync.v4.changes.failedWithoutTransactionWithoutRollback;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;

@ChangeUnit( id="insert-document" , order = "2")
public class BInsertDocument {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(new Document().append("name", "Federico"));
    }
}
