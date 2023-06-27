package io.flamingock.examples.community.changes;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.examples.community.ChangesTracker;
import org.bson.Document;

@ChangeUnit( id="insert-another-document" , order = "4")
public class CInsertAnotherDocument {

    @Execution
    public void execution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        ChangesTracker.add(getClass().getName());
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(clientSession, new Document().append("name", "Jorge"));
    }
}
