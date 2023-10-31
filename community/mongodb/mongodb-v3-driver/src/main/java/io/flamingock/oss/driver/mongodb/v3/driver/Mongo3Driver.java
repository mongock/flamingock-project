package io.flamingock.oss.driver.mongodb.v3.driver;

import com.mongodb.client.MongoClient;
import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.oss.driver.mongodb.v3.MongoDB3Configuration;
import io.flamingock.oss.driver.mongodb.v3.internal.Mongo3Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mongo3Driver implements ConnectionDriver<MongoDB3Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(Mongo3Driver.class);


    private final MongoClient mongoClient;

    private final String databaseName;

    private MongoDB3Configuration driverConfiguration;


    @Deprecated
    public static Mongo3Driver withLockStrategy(MongoClient mongoClient,
                                                String databaseName,
                                                @Deprecated long lockAcquiredForMillis,
                                                @Deprecated long lockQuitTryingAfterMillis,
                                                @Deprecated long lockTryFrequencyMillis) {
        logWarningFieldIgnored("lockAcquiredForMillis", lockAcquiredForMillis);
        logWarningFieldIgnored("lockQuitTryingAfterMillis", lockQuitTryingAfterMillis);
        logWarningFieldIgnored("lockTryFrequencyMillis", lockTryFrequencyMillis);
        return new Mongo3Driver(mongoClient, databaseName);
    }

    @Deprecated
    public static Mongo3Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
        return new Mongo3Driver(mongoClient, databaseName);
    }


    public Mongo3Driver(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public Mongo3Driver setDriverConfiguration(MongoDB3Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, CommunityConfiguration communityConfiguration) {
        return new Mongo3Engine(
                mongoClient,
                databaseName,
                coreConfiguration,
                communityConfiguration,
                driverConfiguration != null ? driverConfiguration : MongoDB3Configuration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
