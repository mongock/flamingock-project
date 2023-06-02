package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.oss.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.oss.core.lock.LockCheckException;
import io.flamingock.oss.core.process.LoadedProcess;
import io.flamingock.oss.core.process.single.SingleExecutableProcess;
import io.mongock.internal.MongockLockAcquirer;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4LockAcquirer extends MongockLockAcquirer {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4LockAcquirer.class);

    private final MongoCollection<Document> collection;

    MongoSync4LockAcquirer(MongoDatabase mongoDatabase, String collectionName) {
        this.collection = mongoDatabase.getCollection(collectionName);
    }

    @Override
    public MongoSync4Lock acquireIfRequired(LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> loadedProcess) throws LockCheckException {
        //TODO implement
        return new MongoSync4Lock();
    }

    @Override
    protected void initialize(boolean indexCreation) {

    }
}
