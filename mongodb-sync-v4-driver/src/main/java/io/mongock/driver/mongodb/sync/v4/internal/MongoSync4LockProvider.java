package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.lock.LockCheckException;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.internal.MongockLockProvider;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4LockProvider extends MongockLockProvider {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4LockProvider.class);

    private final MongoCollection<Document> collection;

    MongoSync4LockProvider(MongoDatabase mongoDatabase, String collectionName) {
        this.collection = mongoDatabase.getCollection(collectionName);
    }

    @Override
    public MongoSync4Lock acquireIfRequired(LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> loadedProcess) throws LockCheckException {
        //TODO implement
        return new MongoSync4Lock();
    }
}
