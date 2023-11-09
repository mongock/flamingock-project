package io.flamingock.oss.driver.mongodb.springdata.v4.internal;

import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.MongoSync4LockRepository;

public class SpringDataMongoV4LockRepository extends MongoSync4LockRepository {

    protected SpringDataMongoV4LockRepository(MongoDatabase mongoDatabase, String lockCollectionName) {
        super(mongoDatabase, lockCollectionName);
    }
}