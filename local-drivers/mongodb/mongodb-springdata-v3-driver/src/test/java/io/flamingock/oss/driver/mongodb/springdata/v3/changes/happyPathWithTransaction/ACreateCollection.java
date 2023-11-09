package io.flamingock.oss.driver.mongodb.springdata.v3.changes.happyPathWithTransaction;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection("clientCollection");
    }
}
