package io.flamingock.oss.driver.couchbase.driver;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.oss.driver.couchbase.CouchbaseConfiguration;
import io.flamingock.oss.driver.couchbase.internal.CouchbaseEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchbaseDriver implements ConnectionDriver<CouchbaseConfiguration> {
    
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseDriver.class);

    private final Collection collection;
    private final Cluster cluster;

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


    public CouchbaseDriver(Cluster cluster, Collection collection) {
        this.cluster = cluster;
        this.collection = collection;
    }

    @Override
    public CouchbaseDriver setDriverConfiguration(CouchbaseConfiguration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        return this;
    }

    @Override
    public ConnectionEngine getConnectionEngine(CoreConfiguration coreConfiguration, CommunityConfiguration communityConfiguration) {
        return new CouchbaseEngine(
                cluster,
                collection,
                coreConfiguration,
                communityConfiguration,
                driverConfiguration != null ? driverConfiguration : CouchbaseConfiguration.getDefault());
    }

    private static void logWarningFieldIgnored(String name, long value) {
        logger.warn("Parameter[{}] with value[{}] will be ignored. It needs to be injected in the configuration",
                name, value);
    }

}
