package io.flamingock.oss.driver.mongodb.springdata.v3.internal;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v3.config.SpringDataMongoV3Configuration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.audit.Auditor;
import io.flamingock.community.internal.lock.LocalLockAcquirer;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ReadConcern;

public class SpringDataMongoV3Engine implements ConnectionEngine {

    private final MongoTemplate mongoTemplate;
    private final CommunityConfiguration communityConfiguration;

    private SpringDataMongoV3Auditor auditor;
    private LocalLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final SpringDataMongoV3Configuration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public SpringDataMongoV3Engine(MongoTemplate mongoTemplate,
                            CoreConfiguration coreConfiguration,
                            CommunityConfiguration communityConfiguration,
                            SpringDataMongoV3Configuration driverConfiguration) {
        this.mongoTemplate = mongoTemplate;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.communityConfiguration = communityConfiguration;
    }

    @Override
    public void initialize() {
        ReadWriteConfiguration readWriteConfiguration = new ReadWriteConfiguration(driverConfiguration.getBuiltMongoDBWriteConcern(),
                    new ReadConcern(driverConfiguration.getReadConcern()),
                    driverConfiguration.getReadPreference().getValue());
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new SpringDataMongoV3TransactionWrapper(mongoTemplate, readWriteConfiguration) : null;
        auditor = new SpringDataMongoV3Auditor(mongoTemplate,
                driverConfiguration.getMigrationRepositoryName(),
                readWriteConfiguration);
        auditor.initialize(driverConfiguration.isIndexCreation());
        SpringDataMongoV3LockRepository lockRepository = new SpringDataMongoV3LockRepository(mongoTemplate.getDb(), driverConfiguration.getLockRepositoryName());
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
