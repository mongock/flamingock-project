package io.mongock.driver.mongodb.sync.v4.internal;

import io.mongock.core.lock.Lock;
import io.mongock.core.lock.LockStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4Lock implements Lock {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4Lock.class);


    @Override
    public void ensureLock() {
        //TODO implement
    }

    @Override
    public LockStatus getStatus() {
        //TODO implement
        return LockStatus.ACQUIRED;
    }

    @Override
    public void close() throws Exception {
        //TODO implement

    }
}
