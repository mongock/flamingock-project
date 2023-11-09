package io.flamingock.oss.driver.mongodb.springdata.v4.changes.happyPathWithTransaction;

import com.mongodb.client.MongoCollection;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit( id="insert-another-document" , order = "3")
public class CInsertAnotherDocument {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("clientCollection");
        collection.insertOne(new Document().append("name", "Jorge"));
    }
}
