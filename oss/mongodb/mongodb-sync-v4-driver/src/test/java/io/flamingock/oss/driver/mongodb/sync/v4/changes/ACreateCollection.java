package io.flamingock.oss.driver.mongodb.sync.v4.changes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("clientCollection");
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        System.out.println("clientCollection with documents: " + collection.countDocuments());
    }
}
