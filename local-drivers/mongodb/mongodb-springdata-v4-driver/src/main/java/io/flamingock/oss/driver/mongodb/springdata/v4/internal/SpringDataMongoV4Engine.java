/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.oss.driver.mongodb.springdata.v4.internal;

import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v4.config.SpringDataMongoV4Configuration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.audit.Auditor;
import io.flamingock.community.internal.lock.LocalLockAcquirer;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ReadConcern;

public class SpringDataMongoV4Engine implements ConnectionEngine {

    private final MongoTemplate mongoTemplate;
    private final LocalConfigurable localConfiguration;

    private SpringDataMongoV4Auditor auditor;
    private LocalLockAcquirer lockProvider;
    private TransactionWrapper transactionWrapper;
    private final SpringDataMongoV4Configuration driverConfiguration;
    private final CoreConfigurable coreConfiguration;


    public SpringDataMongoV4Engine(MongoTemplate mongoTemplate,
                                   CoreConfigurable coreConfiguration,
                                   LocalConfigurable localConfiguration,
                            SpringDataMongoV4Configuration driverConfiguration) {
        this.mongoTemplate = mongoTemplate;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
        this.localConfiguration = localConfiguration;
    }

    @Override
    public void initialize() {
        ReadWriteConfiguration readWriteConfiguration = new ReadWriteConfiguration(driverConfiguration.getBuiltMongoDBWriteConcern(),
                    new ReadConcern(driverConfiguration.getReadConcern()),
                    driverConfiguration.getReadPreference().getValue());
        transactionWrapper = coreConfiguration.getTransactionEnabled() ? new SpringDataMongoV4TransactionWrapper(mongoTemplate, readWriteConfiguration) : null;
        auditor = new SpringDataMongoV4Auditor(mongoTemplate,
                driverConfiguration.getMigrationRepositoryName(),
                readWriteConfiguration);
        auditor.initialize(driverConfiguration.isIndexCreation());
        SpringDataMongoV4LockRepository lockRepository = new SpringDataMongoV4LockRepository(mongoTemplate.getDb(), driverConfiguration.getLockRepositoryName());
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
