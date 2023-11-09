package io.flamingock.oss.driver.couchbase.internal;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.RemoveOptions;
import com.couchbase.client.java.kv.ReplaceOptions;
import io.flamingock.community.internal.lock.LockEntry;
import io.flamingock.community.internal.lock.LockPersistenceException;
import io.flamingock.community.internal.lock.LockRepository;
import io.flamingock.core.util.TimeUtil;
import io.flamingock.oss.driver.couchbase.internal.util.CouchBaseUtil;
import io.flamingock.oss.driver.couchbase.internal.util.LockEntryKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static io.flamingock.community.internal.lock.LockEntryField.EXPIRES_AT_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.KEY_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.OWNER_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.STATUS_FIELD;
import static io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants.DOCUMENT_TYPE_KEY;
import static io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants.DOCUMENT_TYPE_LOCK_ENTRY;

public class CouchbaseLockRepository implements LockRepository {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLockRepository.class);

    private static final Set<String> QUERY_FIELDS = Collections.emptySet();

    protected final Collection collection;
    protected final Cluster cluster;
    protected final CouchbaseGenericRepository couchbaseGenericRepository;

    private final LockEntryKeyGenerator keyGenerator = new LockEntryKeyGenerator();

    protected CouchbaseLockRepository(Cluster cluster, Collection collection) {
        this.cluster = cluster;
        this.collection = collection;
        this.couchbaseGenericRepository = new CouchbaseGenericRepository(cluster, collection, QUERY_FIELDS);
    }

    protected void initialize(boolean indexCreation) {
        this.couchbaseGenericRepository.initialize(indexCreation);
    }

    /**
     * Only for testing
     */
    @Override
    public void deleteAll() {
        this.couchbaseGenericRepository.deleteAll();
    }

    @Override
    public void upsert(LockEntry newLock) throws LockPersistenceException {
        String key = keyGenerator.toKey(newLock);
        try {
            GetResult result = collection.get(key);
            LockEntry existingLock = CouchBaseUtil.lockEntryFromEntity(result.contentAsObject());
            if (newLock.getOwner().equals(existingLock.getOwner()) ||
                    LocalDateTime.now().isAfter(existingLock.getExpiresAt())) {
                logger.debug("Lock with key {} already owned by us or is expired, so trying to perform a lock.",
                        existingLock.getKey());
                collection.replace(key, toEntity(newLock), ReplaceOptions.replaceOptions().cas(result.cas()));
                logger.debug("Lock with key {} updated", key);
            } else if (LocalDateTime.now().isBefore(existingLock.getExpiresAt())) {
                logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(),
                        existingLock.getExpiresAt());
                throw new LockPersistenceException("Get By" + key, newLock.toString(),
                        "Still locked by " + existingLock.getOwner() + " until " + existingLock.getExpiresAt());
            }
        } catch (DocumentNotFoundException documentNotFoundException) {
            logger.debug("Lock with key {} does not exist, so trying to perform a lock.", newLock.getKey());
            collection.insert(key, toEntity(newLock));
            logger.debug("Lock with key {} created", key);
        }
    }

    private JsonObject toEntity(LockEntry lockEntry) {
        JsonObject document = JsonObject.create();
        this.couchbaseGenericRepository.addField(document, KEY_FIELD, lockEntry.getKey());
        this.couchbaseGenericRepository.addField(document, OWNER_FIELD, lockEntry.getOwner());
        this.couchbaseGenericRepository.addField(document, STATUS_FIELD, lockEntry.getStatus().name());
        this.couchbaseGenericRepository.addField(document, EXPIRES_AT_FIELD, TimeUtil.toDate(lockEntry.getExpiresAt()));
        this.couchbaseGenericRepository.addField(document, DOCUMENT_TYPE_KEY, DOCUMENT_TYPE_LOCK_ENTRY);
        return document;
    }

    @Override
    public void updateOnlyIfSameOwner(LockEntry newLock) throws LockPersistenceException {
        String key = keyGenerator.toKey(newLock);
        try {
            GetResult result = collection.get(key);
            LockEntry existingLock = CouchBaseUtil.lockEntryFromEntity(result.contentAsObject());
            if (newLock.getOwner().equals(existingLock.getOwner())) {
                logger.debug("Lock with key {} already owned by us, so trying to perform a lock.",
                        existingLock.getKey());
                collection.replace(key, toEntity(newLock), ReplaceOptions.replaceOptions().cas(result.cas()));
                logger.debug("Lock with key {} updated", key);
            } else {
                logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(),
                        existingLock.getExpiresAt());
                throw new LockPersistenceException("Get By " + key, newLock.toString(),
                        "Lock belongs to " + existingLock.getOwner());
            }
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new LockPersistenceException("Get By " + key, newLock.toString(),
                    documentNotFoundException.getMessage());
        }
    }

    @Override
    public LockEntry findByKey(String lockKey) {
        String key = keyGenerator.toKey(lockKey);
        try {
            GetResult result = collection.get(key);
            return CouchBaseUtil.lockEntryFromEntity(result.contentAsObject());
        } catch (DocumentNotFoundException documentNotFoundException) {
            logger.debug("Lock for key {} was not found.", key);
            return null;
        }
    }

    @Override
    public void removeByKeyAndOwner(String lockKey, String owner) {
        String key = keyGenerator.toKey(lockKey);
        try {
            GetResult result = collection.get(key);
            LockEntry existingLock = CouchBaseUtil.lockEntryFromEntity(result.contentAsObject());
            if (owner.equals(existingLock.getOwner())) {
                logger.debug("Lock for key {} belongs to us, so removing.", key);
                collection.remove(key, RemoveOptions.removeOptions().cas(result.cas()));
            } else {
                logger.debug("Lock for key {} belongs to other owner, can not delete.", key);
            }
        } catch (DocumentNotFoundException documentNotFoundException) {
            logger.debug("Lock for key {} is not found, nothing to do", key);
        }
    }
}
