package io.flamingock.oss.driver.mongodb.v3.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.driver.SingleLockAcquirer;
import io.flamingock.core.core.configurator.CoreConfiguration;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.mongodb.v3.MongoDB3Configuration;

import java.util.Optional;

public class Mongo3Engine implements ConnectionEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;
    private final CommunityConfiguration communityConfiguration;

    private Mongo3Auditor auditor;
    private SingleLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final MongoDB3Configuration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public Mongo3Engine(MongoClient mongoClient,
                            String databaseName,
                            CoreConfiguration coreConfiguration,
                            CommunityConfiguration communityConfiguration,
                            MongoDB3Configuration driverConfiguration) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.communityConfiguration = communityConfiguration;
    }

    @Override
    public void initialize() {
        SessionManager<ClientSession> sessionManager = new SessionManager<>(mongoClient::startSession);
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new Mongo3TransactionWrapper(sessionManager) : null;
        auditor = new Mongo3Auditor(database,
                communityConfiguration.getMigrationRepositoryName(),
                driverConfiguration.getReadWriteConfiguration(),
                sessionManager);
        auditor.initialize(communityConfiguration.isIndexCreation());
        Mongo3LockRepository lockRepository = new Mongo3LockRepository(database, communityConfiguration.getLockRepositoryName());
        lockRepository.initialize(communityConfiguration.isIndexCreation());
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
        return Optional.ofNullable(transactionWrapper);
    }
}
