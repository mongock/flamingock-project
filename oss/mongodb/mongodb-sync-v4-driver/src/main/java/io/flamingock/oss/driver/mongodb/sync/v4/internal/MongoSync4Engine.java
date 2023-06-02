package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.MongoDBSync4Configuration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4SessionManager;
import io.flamingock.oss.internal.MongockConfiguration;
import io.flamingock.oss.internal.driver.ConnectionEngine;
import io.flamingock.oss.internal.driver.MongockAuditor;
import io.flamingock.oss.internal.driver.MongockLockAcquirer;

import java.util.Optional;

public class MongoSync4Engine implements ConnectionEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;

    private MongoSync4Auditor auditor;
    private MongockLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final MongoDBSync4Configuration driverConfiguration;
    private final MongockConfiguration mongockConfiguration;


    public MongoSync4Engine(MongoClient mongoClient,
                            String databaseName,
                            MongockConfiguration mongockConfiguration,
                            MongoDBSync4Configuration driverConfiguration) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
        this.driverConfiguration = driverConfiguration;
        this.mongockConfiguration = mongockConfiguration;
    }

    @Override
    public void initialize() {
        MongoSync4SessionManager sessionManager = new MongoSync4SessionManager(mongoClient);
        transactionWrapper = new MongoSync4TransactionWrapper(sessionManager);
        auditor = new MongoSync4Auditor(database,
                mongockConfiguration.getMigrationRepositoryName(),
                driverConfiguration.getReadWriteConfiguration(),
                sessionManager);
        auditor.initialize(mongockConfiguration.isIndexCreation());
        MongoSync4LockRepository lockRepository = new MongoSync4LockRepository(database, mongockConfiguration.getLockRepositoryName());
        lockRepository.initialize(mongockConfiguration.isIndexCreation());
        lockProvider = new MongockLockAcquirer(lockRepository, auditor, mongockConfiguration);
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
