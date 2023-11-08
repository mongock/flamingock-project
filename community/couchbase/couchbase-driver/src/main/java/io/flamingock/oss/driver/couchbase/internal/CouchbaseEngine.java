package io.flamingock.oss.driver.couchbase.internal;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.flamingock.community.internal.lock.LocalLockAcquirer;
import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.couchbase.CouchbaseConfiguration;

import java.util.Optional;

public class CouchbaseEngine implements ConnectionEngine {

    private final Collection collection;
    private final Cluster cluster;
    private final LocalConfigurable LocalConfiguration;

    private CouchbaseAuditor auditor;
    private LocalLockAcquirer lockProvider;
    private final CouchbaseConfiguration driverConfiguration;
    private final CoreConfigurable coreConfiguration;


    public CouchbaseEngine(Cluster cluster,
                           Collection collection,
                           CoreConfigurable coreConfiguration,
                           LocalConfigurable LocalConfiguration,
                           CouchbaseConfiguration driverConfiguration) {
        this.cluster = cluster;
        this.collection = collection;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.LocalConfiguration = LocalConfiguration;
    }

    @Override
    public void initialize() {
        auditor = new CouchbaseAuditor(cluster, collection);
        auditor.initialize(driverConfiguration.isIndexCreation());
        CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
        lockRepository.initialize(driverConfiguration.isIndexCreation());
        lockProvider = new LocalLockAcquirer(lockRepository, auditor, coreConfiguration);
    }

    @Override
    public CouchbaseAuditor getAuditor() {
        return auditor;
    }

    @Override
    public LocalLockAcquirer getLockProvider() {
        return lockProvider;
    }


    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.empty();
    }
}
