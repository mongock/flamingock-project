package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4CollectionWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4DocumentWrapper;
import io.flamingock.oss.internal.persistence.LockEntry;
import io.flamingock.oss.internal.persistence.LockEntryField;
import io.flamingock.oss.internal.persistence.LockPersistenceException;
import io.flamingock.oss.internal.persistence.LockRepository;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4LockRepository implements LockRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoSync4LockRepository.class);

    private final MongoCollection<Document> collection;

    MongoSync4LockRepository(MongoDatabase mongoDatabase, String lockCollectionName) {
        this.collection = mongoDatabase.getCollection(lockCollectionName);
    }

    protected void initialize(boolean indexCreation) {
        CollectionInitializator<MongoSync4DocumentWrapper> initializer = new CollectionInitializator<>(
                new MongoSync4CollectionWrapper(collection),
                () -> new MongoSync4DocumentWrapper(new Document()),
                new String[]{LockEntryField.KEY_FIELD}
        );
        if (indexCreation) {
            initializer.initialize();
        } else {
            initializer.justValidateCollection();
        }
    }

    @Override
    public void upsert(LockEntry newLock) throws LockPersistenceException {

    }

    @Override
    public void updateOnlyIfSameOwner(LockEntry newLock) throws LockPersistenceException {

    }

    @Override
    public LockEntry findByKey(String lockKey) {
        return null;
    }

    @Override
    public void removeByKeyAndOwner(String lockKey, String owner) {

    }

    @Override
    public void deleteAll() {

    }
}
