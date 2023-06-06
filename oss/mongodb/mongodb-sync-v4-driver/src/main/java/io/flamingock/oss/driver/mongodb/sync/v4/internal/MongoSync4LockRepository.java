package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4CollectionWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4DocumentWrapper;
import io.flamingock.oss.internal.persistence.LockEntry;
import io.flamingock.oss.internal.persistence.LockPersistenceException;
import io.flamingock.oss.internal.persistence.LockRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static io.flamingock.core.core.lock.LockStatus.LOCK_HELD;
import static io.flamingock.oss.internal.persistence.LockEntryField.*;

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
                new String[]{KEY_FIELD}
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

    protected Bson getAcquireLockQuery(String lockKey, String owner, boolean onlyIfSameOwner) {
        Bson expirationCond = Filters.lt(EXPIRES_AT_FIELD, new Date());
        Bson ownerCond = Filters.eq(OWNER_FIELD, owner);
        Bson keyCond = Filters.eq(KEY_FIELD, lockKey);
        Bson statusCond = Filters.eq(STATUS_FIELD, LOCK_HELD.name());
        return onlyIfSameOwner
                ? Filters.and(keyCond, statusCond, ownerCond)
                : Filters.and(keyCond, Filters.or(expirationCond, ownerCond));
    }
}
