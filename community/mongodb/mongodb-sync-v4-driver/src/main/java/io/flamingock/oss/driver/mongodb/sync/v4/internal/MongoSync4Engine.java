package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.audit.Auditor;
import io.flamingock.community.internal.lock.LocalLockAcquirer;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.mongodb.sync.v4.MongoDBSync4Configuration;

import java.util.Optional;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;
    private final CommunityConfiguration communityConfiguration;

    private MongoSync4Auditor auditor;
    private LocalLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final MongoDBSync4Configuration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public MongoSync4Engine(MongoClient mongoClient,
                            String databaseName,
                            CoreConfiguration coreConfiguration,
                            CommunityConfiguration communityConfiguration,
                            MongoDBSync4Configuration driverConfiguration) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.communityConfiguration = communityConfiguration;
    }

    @Override
    public void initialize() {
        SessionManager<ClientSession> sessionManager = new SessionManager<>(mongoClient::startSession);
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new MongoSync4TransactionWrapper(sessionManager) : null;
        auditor = new MongoSync4Auditor(database,
                driverConfiguration.getMigrationRepositoryName(),
                driverConfiguration.getReadWriteConfiguration(),
                sessionManager);
        auditor.initialize(driverConfiguration.isIndexCreation());
        MongoSync4LockRepository lockRepository = new MongoSync4LockRepository(database, driverConfiguration.getLockRepositoryName());
        lockRepository.initialize(driverConfiguration.isIndexCreation());
        lockProvider = new LocalLockAcquirer(lockRepository, auditor, coreConfiguration);
    }

    @Override
    public Auditor getAuditor() {
        return auditor;
    }

    @Override
    public LocalLockAcquirer getLockProvider() {
        return lockProvider;
    }


    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.ofNullable(transactionWrapper);
    }
}
