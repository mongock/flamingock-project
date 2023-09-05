package io.flamingock.oss.driver.mongodb.springdata.v4.internal;

import io.flamingock.community.internal.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v4.config.SpringDataMongoV4Configuration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.community.internal.driver.ConnectionEngine;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.driver.SingleLockAcquirer;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ReadConcern;

public class SpringDataMongoV4Engine implements ConnectionEngine {

    private final MongoTemplate mongoTemplate;
    private final CommunityConfiguration communityConfiguration;

    private SpringDataMongoV4Auditor auditor;
    private SingleLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final SpringDataMongoV4Configuration driverConfiguration;
    private final CoreConfiguration coreConfiguration;


    public SpringDataMongoV4Engine(MongoTemplate mongoTemplate,
                            CoreConfiguration coreConfiguration,
                            CommunityConfiguration communityConfiguration,
                            SpringDataMongoV4Configuration driverConfiguration) {
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
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new SpringDataMongoV4TransactionWrapper(mongoTemplate, readWriteConfiguration) : null;
        auditor = new SpringDataMongoV4Auditor(mongoTemplate,
                communityConfiguration.getMigrationRepositoryName(),
                readWriteConfiguration);
        auditor.initialize(communityConfiguration.isIndexCreation());
        SpringDataMongoV4LockRepository lockRepository = new SpringDataMongoV4LockRepository(mongoTemplate.getDb(), communityConfiguration.getLockRepositoryName());
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
