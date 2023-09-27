package io.flamingock.examples.community.changes;

import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;

@ChangeUnit( id="create-collection" , order = "1", transactional = false)
public class ACreateCollection {

    @Execution
    public void execution(@NonLockGuarded(NonLockGuardedType.NONE) MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("clientCollection");
    }
}
