package io.flamingock.oss.driver.couchbase.internal;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.driver.SingleLockAcquirer;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.couchbase.CouchbaseConfiguration;

import java.util.Optional;

public class CouchbaseEngine implements ConnectionEngine {

    private final Collection collection;
    private final Cluster cluster;
    private final CommunityConfiguration communityConfiguration;

    private CouchbaseAuditor auditor;
    private SingleLockAcquirer lockProvider;
    private final CouchbaseConfiguration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public CouchbaseEngine(Cluster cluster, 
                            Collection collection,
                            CoreConfiguration coreConfiguration,
                            CommunityConfiguration communityConfiguration,
                            CouchbaseConfiguration driverConfiguration) {
        this.cluster = cluster;
        this.collection = collection;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.communityConfiguration = communityConfiguration;
    }

    @Override
    public void initialize() {
        auditor = new CouchbaseAuditor(cluster, collection);
        auditor.initialize(driverConfiguration.isIndexCreation());
        CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
        lockRepository.initialize(driverConfiguration.isIndexCreation());
        lockProvider = new SingleLockAcquirer(lockRepository, auditor, coreConfiguration);
    }

    @Override
    public MongockAuditor getAuditor() {
        return auditor;
    }

    @Override
    public SingleLockAcquirer getLockProvider() {
        return lockProvider;
    }


    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.empty();
    }
}
