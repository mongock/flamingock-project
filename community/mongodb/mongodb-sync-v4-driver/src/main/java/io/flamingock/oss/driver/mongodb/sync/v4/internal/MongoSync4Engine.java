package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.driver.MongockLockAcquirer;

import java.util.Optional;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;

    private MongoSync4Auditor auditor;
    private MongockLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final MongoDBSync4Configuration driverConfiguration;
    private final CommunityConfiguration communityConfiguration;


    public MongoSync4Engine(MongoClient mongoClient,
                            String databaseName,
                            CommunityConfiguration communityConfiguration,
                            MongoDBSync4Configuration driverConfiguration) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
        this.driverConfiguration = driverConfiguration;
        this.communityConfiguration = communityConfiguration;
    }

    @Override
    public void initialize() {
        SessionManager<ClientSession> sessionManager = new SessionManager<>(mongoClient::startSession);
        transactionWrapper = new MongoSync4TransactionWrapper(sessionManager);
        auditor = new MongoSync4Auditor(database,
                communityConfiguration.getMigrationRepositoryName(),
                driverConfiguration.getReadWriteConfiguration(),
                sessionManager);
        auditor.initialize(communityConfiguration.isIndexCreation());
        MongoSync4LockRepository lockRepository = new MongoSync4LockRepository(database, communityConfiguration.getLockRepositoryName());
        lockRepository.initialize(communityConfiguration.isIndexCreation());
        lockProvider = new MongockLockAcquirer(lockRepository, auditor, communityConfiguration);
    }

    @Override
    public MongockAuditor getAuditor() {
        return auditor;
    }

    @Override
    public MongockLockAcquirer getLockProvider() {
        return lockProvider;
    }


    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.of(transactionWrapper);
    }
}
