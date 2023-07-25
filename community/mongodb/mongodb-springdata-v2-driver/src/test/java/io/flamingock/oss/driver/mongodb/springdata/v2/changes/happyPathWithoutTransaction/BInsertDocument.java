package io.flamingock.oss.driver.mongodb.springdata.v2.changes.happyPathWithoutTransaction;

import com.mongodb.client.MongoCollection;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit( id="insert-document" , order = "2")
public class BInsertDocument {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("clientCollection");
        collection.insertOne(new Document().append("name", "Federico"));
    }
}
