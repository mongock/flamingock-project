package io.flamingock.oss.driver.mongodb.springdata.v2.driver;

import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.oss.driver.mongodb.springdata.v2.config.SpringDataMongoV2Configuration;
import io.flamingock.oss.driver.mongodb.springdata.v2.internal.SpringDataMongoV2Engine;
import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongoV2Driver implements ConnectionDriver<SpringDataMongoV2Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2Driver.class);


    private final MongoTemplate mongoTemplate;

    private SpringDataMongoV2Configuration driverConfiguration;


    @Deprecated
    public static SpringDataMongoV2Driver withLockStrategy(MongoTemplate mongoTemplate,
                                                    @Deprecated long lockAcquiredForMillis,
                                                    @Deprecated long lockQuitTryingAfterMillis,
                                                    @Deprecated long lockTryFrequencyMillis) {
        logWarningFieldIgnored("lockAcquiredForMillis", lockAcquiredForMillis);
        logWarningFieldIgnored("lockQuitTryingAfterMillis", lockQuitTryingAfterMillis);
        logWarningFieldIgnored("lockTryFrequencyMillis", lockTryFrequencyMillis);
        return new SpringDataMongoV2Driver(mongoTemplate);
    }

    @Deprecated
    public static SpringDataMongoV2Driver withDefaultLock(MongoTemplate mongoTemplate) {
        return new SpringDataMongoV2Driver(mongoTemplate);
    }


    public SpringDataMongoV2Driver(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SpringDataMongoV2Driver setDriverConfiguration(SpringDataMongoV2Configuration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(CoreConfigurable coreConfiguration, LocalConfiguration communityConfiguration) {
        return new SpringDataMongoV2Engine(
                mongoTemplate,
                coreConfiguration,
                communityConfiguration,
                driverConfiguration != null ? driverConfiguration : SpringDataMongoV2Configuration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
