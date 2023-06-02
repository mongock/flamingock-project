package io.mongock.driver.mongodb.sync.v4.driver.changes;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
//        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
//        System.out.println("clientCollection with documents: " + collection.countDocuments());
    }
}
