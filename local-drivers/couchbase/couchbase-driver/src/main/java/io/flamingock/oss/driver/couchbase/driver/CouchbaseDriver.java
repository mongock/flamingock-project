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

package io.flamingock.oss.driver.couchbase.driver;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.builder.core.CoreConfigurable;
import io.flamingock.core.builder.local.CommunityConfigurable;
import io.flamingock.core.community.driver.LocalDriver;
import io.flamingock.core.community.LocalEngine;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.oss.driver.couchbase.CouchbaseConfiguration;
import io.flamingock.oss.driver.couchbase.internal.CouchbaseEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchbaseDriver implements LocalDriver<CouchbaseConfiguration> {
    
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseDriver.class);

    private Collection collection;
    private Cluster cluster;

    private CouchbaseConfiguration driverConfiguration;


    @Deprecated
    public static CouchbaseDriver withLockStrategy(Cluster cluster, 
                                                Collection collection,
                                                @Deprecated long lockAcquiredForMillis,
                                                @Deprecated long lockQuitTryingAfterMillis,
                                                @Deprecated long lockTryFrequencyMillis) {
        logWarningFieldIgnored("lockAcquiredForMillis", lockAcquiredForMillis);
        logWarningFieldIgnored("lockQuitTryingAfterMillis", lockQuitTryingAfterMillis);
        logWarningFieldIgnored("lockTryFrequencyMillis", lockTryFrequencyMillis);
        return new CouchbaseDriver(cluster, collection);
    }

    @Deprecated
    public static CouchbaseDriver withDefaultLock(Cluster cluster, Collection collection) {
        return new CouchbaseDriver(cluster, collection);
    }

    public CouchbaseDriver() {
    }

    public CouchbaseDriver(Cluster cluster, Collection collection) {
        this.cluster = cluster;
        this.collection = collection;
    }

    @Override
    public void initialize(DependencyContext dependencyContext) {
        this.cluster = (Cluster) dependencyContext
                .getDependency(Cluster.class)
                .orElseThrow(() -> new FlamingockException("DynamoDbClient is needed to be added as dependency"))
                .getInstance();
        this.collection = (Collection) dependencyContext
                .getDependency(Collection.class)
                .orElseThrow(() -> new FlamingockException("DynamoDbClient is needed to be added as dependency"))
                .getInstance();
        dependencyContext.getDependency(CouchbaseConfiguration.class).ifPresent(dependency -> {
            this.driverConfiguration = (CouchbaseConfiguration) dependency.getInstance();
        });
    }

    @Deprecated
    @Override
    public CouchbaseDriver setDriverConfiguration(CouchbaseConfiguration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public LocalEngine initializeAndGetEngine(RunnerId runnerId, 
                                              CoreConfigurable coreConfiguration, 
                                              CommunityConfigurable localConfiguration) {
        CouchbaseEngine couchbaseEngine = new CouchbaseEngine(
                cluster,
                collection,
                coreConfiguration,
                localConfiguration,
                driverConfiguration != null ? driverConfiguration : CouchbaseConfiguration.getDefault());
        couchbaseEngine.initialize(runnerId);
        return couchbaseEngine;
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
