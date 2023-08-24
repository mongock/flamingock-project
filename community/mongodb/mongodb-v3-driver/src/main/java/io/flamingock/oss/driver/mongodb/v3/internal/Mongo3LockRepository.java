package io.flamingock.oss.driver.mongodb.v3.internal;

import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.community.internal.persistence.LockEntry;
import io.flamingock.community.internal.persistence.LockPersistenceException;
import io.flamingock.community.internal.persistence.LockRepository;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.common.mongodb.MongoDBLockMapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.Mongo3CollectionWrapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.Mongo3DocumentWrapper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;

import static io.flamingock.community.internal.persistence.LockEntryField.EXPIRES_AT_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.KEY_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.OWNER_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.STATUS_FIELD;
import static io.flamingock.core.lock.LockStatus.LOCK_HELD;

public class Mongo3LockRepository implements LockRepository {

    private final MongoDBLockMapper<Mongo3DocumentWrapper> mapper = new MongoDBLockMapper<>(() -> new Mongo3DocumentWrapper(new Document()));


    private final MongoCollection<Document> collection;

    protected Mongo3LockRepository(MongoDatabase mongoDatabase, String lockCollectionName) {
        this.collection = mongoDatabase.getCollection(lockCollectionName);
    }

    public void initialize(boolean indexCreation) {
        CollectionInitializator<Mongo3DocumentWrapper> initializer = new CollectionInitializator<>(
                new Mongo3CollectionWrapper(collection),
                () -> new Mongo3DocumentWrapper(new Document()),
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
        insertUpdate(newLock, false);
    }

    @Override
    public void updateOnlyIfSameOwner(LockEntry newLock) throws LockPersistenceException {
        insertUpdate(newLock, true);
    }

    @Override
    public LockEntry findByKey(String lockKey) {
        Document result = collection.find(new Document().append(KEY_FIELD, lockKey)).first();
        if (result != null) {
            return mapper.fromDocument(new Mongo3DocumentWrapper(result));
        }
        return null;
    }

    @Override
    public void removeByKeyAndOwner(String lockKey, String owner) {
        collection.deleteMany(Filters.and(Filters.eq(KEY_FIELD, lockKey), Filters.eq(OWNER_FIELD, owner)));
    }

    @Override
    public void deleteAll() {
        collection.deleteMany(new Document());
    }


    protected void insertUpdate(LockEntry newLock, boolean onlyIfSameOwner)  {
        boolean lockHeld;
        String debErrorDetail = "not db error";
        Bson acquireLockQuery = getAcquireLockQuery(newLock.getKey(), newLock.getOwner(), onlyIfSameOwner);
        Document lockDocument = mapper.toDocument(newLock).getDocument();
        Document newLockDocumentSet = new Document().append("$set", lockDocument);
        try {
            UpdateResult result = collection.updateMany(acquireLockQuery, newLockDocumentSet, new UpdateOptions().upsert(!onlyIfSameOwner));
            lockHeld = result.getModifiedCount() <= 0 && result.getUpsertedId() == null;

        } catch (MongoWriteException ex) {
            lockHeld = ex.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;

            if (!lockHeld) {
                throw ex;
            }
            debErrorDetail = ex.getError().toString();

        } catch (DuplicateKeyException ex) {
            lockHeld = true;
            debErrorDetail = ex.getMessage();
        }

        if (lockHeld) {
            throw new LockPersistenceException(
                    acquireLockQuery.toString(),
                    newLockDocumentSet.toString(),
                    debErrorDetail
            );
        }
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
