/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.oss.driver.mongodb.sync.v4.driver;

import com.mongodb.client.MongoClient;
import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.oss.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.MongoSync4Engine;
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
    public ConnectionEngine getConnectionEngine(CoreConfigurable coreConfiguration, LocalConfigurable communityConfiguration) {
        return new MongoSync4Engine(
                mongoClient,
                databaseName,
                coreConfiguration,
                communityConfiguration,
                driverConfiguration != null ? driverConfiguration : MongoDBSync4Configuration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}