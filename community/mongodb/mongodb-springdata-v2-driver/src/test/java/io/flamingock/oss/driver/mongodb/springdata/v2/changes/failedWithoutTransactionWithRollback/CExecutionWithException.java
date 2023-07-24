package io.flamingock.oss.driver.mongodb.springdata.v2.changes.failedWithoutTransactionWithRollback;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoCollection;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;

@ChangeUnit( id="execution-with-exception" , order = "3")
public class CExecutionWithException {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("clientCollection");
        collection.insertOne(new Document().append("name", "Jorge"));
        throw new RuntimeException("test");
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("clientCollection");
        collection.deleteOne(new Document().append("name", "Jorge"));
    }
}
