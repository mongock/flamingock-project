package io.flamingock.oss.driver.mongodb.springdata.v3.changes.failedWithoutTransactionWithoutRollback;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection("clientCollection");
    }
}
