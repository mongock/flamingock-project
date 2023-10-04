package io.flamingock.examples.community.changes;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.springframework.context.annotation.Profile;

@Profile("accepted-profile-1")
@ChangeUnit(id = "create-collection", order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("clientCollection");
    }
}