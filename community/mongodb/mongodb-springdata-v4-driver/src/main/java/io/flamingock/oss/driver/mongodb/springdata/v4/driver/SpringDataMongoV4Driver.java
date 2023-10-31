package io.flamingock.oss.driver.mongodb.springdata.v4.driver;

import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.oss.driver.mongodb.springdata.v4.config.SpringDataMongoV4Configuration;
import io.flamingock.oss.driver.mongodb.springdata.v4.internal.SpringDataMongoV4Engine;
import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongoV4Driver implements ConnectionDriver<SpringDataMongoV4Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV4Driver.class);


    private final MongoTemplate mongoTemplate;

    private SpringDataMongoV4Configuration driverConfiguration;


    @Deprecated
    public static SpringDataMongoV4Driver withLockStrategy(MongoTemplate mongoTemplate,
                                                    @Deprecated long lockAcquiredForMillis,
                                                    @Deprecated long lockQuitTryingAfterMillis,
                                                    @Deprecated long lockTryFrequencyMillis) {
        logWarningFieldIgnored("lockAcquiredForMillis", lockAcquiredForMillis);
        logWarningFieldIgnored("lockQuitTryingAfterMillis", lockQuitTryingAfterMillis);
        logWarningFieldIgnored("lockTryFrequencyMillis", lockTryFrequencyMillis);
        return new SpringDataMongoV4Driver(mongoTemplate);
    }

    @Deprecated
    public static SpringDataMongoV4Driver withDefaultLock(MongoTemplate mongoTemplate) {
        return new SpringDataMongoV4Driver(mongoTemplate);
    }


    public SpringDataMongoV4Driver(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SpringDataMongoV4Driver setDriverConfiguration(SpringDataMongoV4Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, CommunityConfiguration communityConfiguration) {
        return new SpringDataMongoV4Engine(
                mongoTemplate,
                coreConfiguration,
                communityConfiguration,
                driverConfiguration != null ? driverConfiguration : SpringDataMongoV4Configuration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
