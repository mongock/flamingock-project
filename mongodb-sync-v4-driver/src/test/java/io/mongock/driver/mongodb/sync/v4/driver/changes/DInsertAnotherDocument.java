package io.mongock.driver.mongodb.sync.v4.driver.changes;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.api.annotations.ChangeUnit;
import io.flamingock.oss.api.annotations.Execution;
import org.bson.Document;

@ChangeUnit( id="insert-another-document" , order = "4")
public class DInsertAnotherDocument {

    @Execution
    public void execution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(clientSession, new Document().append("name", "Jorge from another document"));
    }
}
