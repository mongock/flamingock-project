package io.flamingock.oss.driver.mongodb.springdata.v2.internal;

import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v2.config.SpringDataMongoV2Configuration;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.audit.Auditor;
import io.flamingock.community.internal.lock.LocalLockAcquirer;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ReadConcern;

public class SpringDataMongoV2Engine implements ConnectionEngine {

    private final MongoTemplate mongoTemplate;
    private final LocalConfiguration communityConfiguration;

    private SpringDataMongoV2Auditor auditor;
    private LocalLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final SpringDataMongoV2Configuration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public SpringDataMongoV2Engine(MongoTemplate mongoTemplate,
                            CoreConfiguration coreConfiguration,
                            LocalConfiguration communityConfiguration,
                            SpringDataMongoV2Configuration driverConfiguration) {
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
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new SpringDataMongoV2TransactionWrapper(mongoTemplate, readWriteConfiguration) : null;
        auditor = new SpringDataMongoV2Auditor(mongoTemplate,
                driverConfiguration.getMigrationRepositoryName(),
                readWriteConfiguration);
        auditor.initialize(driverConfiguration.isIndexCreation());
        SpringDataMongoV2LockRepository lockRepository = new SpringDataMongoV2LockRepository(mongoTemplate.getDb(), driverConfiguration.getLockRepositoryName());
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
