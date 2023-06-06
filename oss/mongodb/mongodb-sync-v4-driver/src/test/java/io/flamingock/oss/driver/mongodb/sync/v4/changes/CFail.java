package io.flamingock.oss.driver.mongodb.sync.v4.changes;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;

@ChangeUnit( id="fail" , order = "3")
public class CFail {

    @Execution
    public void execution(MongoDatabase mongoDatabase, ClientSession clientSession) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("clientCollection");
        collection.insertOne(clientSession, new Document().append("name", "Juan Failed(ClientSession)"));
        collection.insertOne(new Document().append("name", "Juan Failed(NO ClientSession)"));
//        if(true) {
//            throw new RuntimeException("EXPECTED EXCEPTION");
//        }
    }
}
