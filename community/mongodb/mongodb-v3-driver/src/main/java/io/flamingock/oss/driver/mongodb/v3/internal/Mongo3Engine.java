package io.flamingock.oss.driver.mongodb.v3.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.mongodb.v3.MongoDB3Configuration;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.driver.MongockLockAcquirer;

import java.util.Optional;

public class Mongo3Engine implements ConnectionEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;
    private final CommunityProperties communityProperties;

    private Mongo3Auditor auditor;
    private MongockLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final MongoDB3Configuration driverConfiguration;
    private final CoreProperties coreProperties;


    public Mongo3Engine(MongoClient mongoClient,
                            String databaseName,
                            CoreProperties coreProperties,
                            CommunityProperties communityProperties,
                            MongoDB3Configuration driverConfiguration) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
        this.driverConfiguration = driverConfiguration;
        this.coreProperties = coreProperties;
        this.communityProperties = communityProperties;
    }

    @Override
    public void initialize() {
        SessionManager<ClientSession> sessionManager = new SessionManager<>(mongoClient::startSession);
        transactionWrapper = coreProperties.getTransactionEnabled() ? new Mongo3TransactionWrapper(sessionManager) : null;
        auditor = new Mongo3Auditor(database,
                communityProperties.getMigrationRepositoryName(),
                driverConfiguration.getReadWriteConfiguration(),
                sessionManager);
        auditor.initialize(communityProperties.isIndexCreation());
        Mongo3LockRepository lockRepository = new Mongo3LockRepository(database, communityProperties.getLockRepositoryName());
        lockRepository.initialize(communityProperties.isIndexCreation());
        lockProvider = new MongockLockAcquirer(lockRepository, auditor, coreProperties);
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
        return Optional.ofNullable(transactionWrapper);
    }
}
