package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import io.flamingock.oss.core.lock.Lock;
import io.flamingock.oss.core.lock.LockStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4Lock implements Lock {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4Lock.class);


    @Override
    public void ensureLock() {
        logger.info("\n*******************************ENSURING LOCK***********************\n");
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
        logger.info("\n*******************************RELASING LOCK***********************\n");

    }
}
