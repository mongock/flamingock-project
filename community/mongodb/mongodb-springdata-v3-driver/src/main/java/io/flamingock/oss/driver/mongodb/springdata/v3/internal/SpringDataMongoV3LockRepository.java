package io.flamingock.oss.driver.mongodb.springdata.v3.internal;

import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.MongoSync4LockRepository;

public class SpringDataMongoV3LockRepository extends MongoSync4LockRepository {

    public SpringDataMongoV3LockRepository(MongoDatabase mongoDatabase, String lockCollectionName) {
        super(mongoDatabase, lockCollectionName);
    }
}