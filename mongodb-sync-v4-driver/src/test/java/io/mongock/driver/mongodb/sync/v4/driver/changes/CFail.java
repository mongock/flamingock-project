package io.mongock.driver.mongodb.sync.v4.driver.changes;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
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
