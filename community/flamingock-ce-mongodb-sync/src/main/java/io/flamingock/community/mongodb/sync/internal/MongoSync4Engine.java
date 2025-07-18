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

package io.flamingock.community.mongodb.sync.internal;

import com.mongodb.ReadConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.cloud.transaction.mongodb.sync.config.MongoDBSync4Configuration;
import io.flamingock.cloud.transaction.mongodb.sync.wrapper.MongoSync4TransactionWrapper;
import io.flamingock.internal.util.id.RunnerId;
import io.flamingock.internal.util.TimeService;
import io.flamingock.internal.core.builder.core.CoreConfigurable;
import io.flamingock.internal.core.builder.local.CommunityConfigurable;
import io.flamingock.internal.core.community.AbstractLocalEngine;
import io.flamingock.internal.core.community.LocalAuditor;
import io.flamingock.internal.core.community.LocalExecutionPlanner;
import io.flamingock.internal.core.community.TransactionManager;
import io.flamingock.internal.core.transaction.TransactionWrapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MongoSync4Engine extends AbstractLocalEngine {

    private final MongoDatabase database;
    private final MongoClient mongoClient;
    private final MongoDBSync4Configuration driverConfiguration;
    private final CoreConfigurable coreConfiguration;
    private MongoSync4Auditor auditor;
    private LocalExecutionPlanner executionPlanner;
    private TransactionWrapper transactionWrapper;

    public MongoSync4Engine(MongoClient mongoClient,
                            MongoDatabase database,
                            CoreConfigurable coreConfiguration,
                            CommunityConfigurable localConfiguration,
                            MongoDBSync4Configuration driverConfiguration) {
        super(localConfiguration);
        this.mongoClient = mongoClient;
        this.database = database;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    protected void doInitialize(RunnerId runnerId) {
        TransactionManager<ClientSession> sessionManager = new TransactionManager<>(mongoClient::startSession);

        transactionWrapper = localConfiguration.isTransactionDisabled()
                ? null
                : new MongoSync4TransactionWrapper(sessionManager);

        //Auditor
        auditor = buildAuditor(sessionManager);
        auditor.initialize(driverConfiguration.isAutoCreate());

        //Lock
        MongoSync4LockService lockService = buildLockService();
        lockService.initialize(driverConfiguration.isAutoCreate());
        executionPlanner = new LocalExecutionPlanner(runnerId, lockService, auditor, coreConfiguration);
    }

    @Override
    public LocalAuditor getAuditWriter() {
        return auditor;
    }

    @Override
    public LocalExecutionPlanner getExecutionPlanner() {
        return executionPlanner;
    }

    @Override
    public Set<Class<?>> getNonGuardedTypes() {
        return new HashSet<>(Collections.singletonList(ClientSession.class));
    }

    @Override
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.ofNullable(transactionWrapper);
    }



    private MongoSync4Auditor buildAuditor(TransactionManager<ClientSession> sessionManager) {
        return new MongoSync4Auditor(
                database,
                driverConfiguration.getAuditRepositoryName(),
                new ReadWriteConfiguration(
                        driverConfiguration.getBuiltMongoDBWriteConcern(),
                        new ReadConcern(driverConfiguration.getReadConcern()),
                        driverConfiguration.getReadPreference().getValue()
                ),
                sessionManager);
    }

    private MongoSync4LockService buildLockService() {
        return new MongoSync4LockService(
                database,
                driverConfiguration.getLockRepositoryName(),
                new ReadWriteConfiguration(
                        driverConfiguration.getBuiltMongoDBWriteConcern(),
                        new ReadConcern(driverConfiguration.getReadConcern()),
                        driverConfiguration.getReadPreference().getValue()
                ),
                TimeService.getDefault());
    }

}
