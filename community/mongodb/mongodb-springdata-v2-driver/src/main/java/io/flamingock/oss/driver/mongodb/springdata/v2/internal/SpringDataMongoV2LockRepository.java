package io.flamingock.oss.driver.mongodb.springdata.v2.internal;

import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.driver.mongodb.v3.internal.Mongo3LockRepository;

public class SpringDataMongoV2LockRepository extends Mongo3LockRepository {

    protected SpringDataMongoV2LockRepository(MongoDatabase mongoDatabase, String lockCollectionName) {
        super(mongoDatabase, lockCollectionName);
    }
}