package io.flamingock.oss.driver.mongodb.v3.changes.happyPathWithTransaction;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("clientCollection");
    }
}
