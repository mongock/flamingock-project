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

package io.flamingock.oss.driver.mongodb.springdata.v2.internal;

import com.mongodb.ReadConcern;
import com.mongodb.client.MongoCollection;
import io.flamingock.commons.utils.id.RunnerId;
import io.flamingock.internal.core.builder.core.CoreConfigurable;
import io.flamingock.internal.core.builder.local.CommunityConfigurable;
import io.flamingock.internal.core.community.AbstractLocalEngine;
import io.flamingock.internal.core.community.LocalAuditor;
import io.flamingock.internal.core.community.LocalExecutionPlanner;
import io.flamingock.internal.core.engine.audit.importer.ImporterModule;
import io.flamingock.internal.core.system.SystemModule;
import io.flamingock.internal.core.transaction.TransactionWrapper;
import io.flamingock.importer.mongodb.v3.MongoImporterReader;
import io.flamingock.oss.driver.mongodb.springdata.v2.config.SpringDataMongoV2Configuration;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public class SpringDataMongoV2Engine extends AbstractLocalEngine {

    private final MongoTemplate mongoTemplate;

    private SpringDataMongoV2Auditor auditor;
    private LocalExecutionPlanner executionPlanner;
    private TransactionWrapper transactionWrapper;
    private SystemModule mongockImporter = null;
    private final SpringDataMongoV2Configuration driverConfiguration;
    private final CoreConfigurable coreConfiguration;


    public SpringDataMongoV2Engine(MongoTemplate mongoTemplate,
                                   CoreConfigurable coreConfiguration,
                                   CommunityConfigurable localConfiguration,
                                   SpringDataMongoV2Configuration driverConfiguration) {
        super(localConfiguration);
        this.mongoTemplate = mongoTemplate;
        this.driverConfiguration = driverConfiguration;
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    protected void doInitialize(RunnerId runnerId) {
        ReadWriteConfiguration readWriteConfiguration = new ReadWriteConfiguration(driverConfiguration.getBuiltMongoDBWriteConcern(),
                new ReadConcern(driverConfiguration.getReadConcern()),
                driverConfiguration.getReadPreference().getValue());
        transactionWrapper = localConfiguration.isTransactionDisabled()
                ? null
                : new SpringDataMongoV2TransactionWrapper(mongoTemplate, readWriteConfiguration);

        auditor = new SpringDataMongoV2Auditor(
                mongoTemplate,
                driverConfiguration.getAuditRepositoryName(),
                readWriteConfiguration);
        auditor.initialize(driverConfiguration.isAutoCreate());
        SpringDataMongoV2LockService lockService = new SpringDataMongoV2LockService(
                mongoTemplate.getDb(),
                driverConfiguration.getLockRepositoryName(),
                readWriteConfiguration);
        lockService.initialize(driverConfiguration.isAutoCreate());
        executionPlanner = new LocalExecutionPlanner(runnerId, lockService, auditor, coreConfiguration);
        //Mongock importer
        if (coreConfiguration.isMongockImporterEnabled()) {
            MongoCollection<Document> legacyCollectionToImportFrom = mongoTemplate
                    .getDb()
                    .getCollection(coreConfiguration.getLegacyMongockChangelogSource());
            MongoImporterReader importerReader = new MongoImporterReader(legacyCollectionToImportFrom);
            mongockImporter = new ImporterModule(importerReader);
        }
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
    public Optional<TransactionWrapper> getTransactionWrapper() {
        return Optional.ofNullable(transactionWrapper);
    }

    @Override
    protected Optional<SystemModule> getMongockLegacyImporterModule() {
        return Optional.ofNullable(mongockImporter);
    }
}
