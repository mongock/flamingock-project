package io.flamingock.examples.community.changes;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;

@ChangeUnit( id="insert-document" , order = "2")
public class BInsertDocument {
    @Execution
    public void execution(@NonLockGuarded(NonLockGuardedType.NONE)MongoDatabase mongoDatabase,@NonLockGuarded(NonLockGuardedType.NONE)ClientSession clientSession) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(clientSession, new Document().append("name", "Federico"));
    }
}
