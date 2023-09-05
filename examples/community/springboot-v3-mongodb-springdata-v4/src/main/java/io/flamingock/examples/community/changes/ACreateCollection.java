package io.flamingock.examples.community.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.examples.community.ChangesTracker;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "create-collection", order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        ChangesTracker.add(getClass().getName());
        mongoTemplate.createCollection("clientCollection");
    }
}
