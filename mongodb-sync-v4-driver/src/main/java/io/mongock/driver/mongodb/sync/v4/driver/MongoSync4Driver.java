package io.mongock.driver.mongodb.sync.v4.driver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.mongock.driver.mongodb.sync.v4.internal.MongoSync4Engine;
import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.driver.ConnectionDriver;
import io.mongock.internal.driver.ConnectionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSync4Driver implements ConnectionDriver<MongoDBSync4Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(MongoSync4Driver.class);


    private final MongoClient mongoClient;

    private final String databaseName;

    private MongoDBSync4Configuration driverConfiguration;


    @Deprecated
    public static MongoSync4Driver withLockStrategy(MongoClient mongoClient,
                                                    String databaseName,
                                                    @Deprecated long lockAcquiredForMillis,
                                                    @Deprecated long lockQuitTryingAfterMillis,
                                                    @Deprecated long lockTryFrequencyMillis) {
        logWarningFieldIgnored("lockAcquiredForMillis", lockAcquiredForMillis);
        logWarningFieldIgnored("lockQuitTryingAfterMillis", lockQuitTryingAfterMillis);
        logWarningFieldIgnored("lockTryFrequencyMillis", lockTryFrequencyMillis);
        return new MongoSync4Driver(mongoClient, databaseName);
    }

    @Deprecated
    public static MongoSync4Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
        return new MongoSync4Driver(mongoClient, databaseName);
    }


    public MongoSync4Driver(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public MongoSync4Driver setDriverConfiguration(MongoDBSync4Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(MongockConfiguration mongockConfiguration) {
        return new MongoSync4Engine(
                mongoClient,
                databaseName,
                mongockConfiguration,
                driverConfiguration != null ? driverConfiguration : MongoDBSync4Configuration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
