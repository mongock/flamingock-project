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

package io.flamingock.oss.driver.mongodb.springdata.v2;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.commons.utils.Trio;
import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.engine.audit.importer.changeunit.MongockImporterChangeUnit;
import io.flamingock.core.engine.audit.writer.AuditEntry;
import io.flamingock.core.legacy.MongockLegacyIdGenerator;
import io.flamingock.core.processor.util.Deserializer;
import io.flamingock.oss.driver.mongodb.springdata.v2.changes._0_mongock_create_authors_collection;
import io.flamingock.oss.driver.mongodb.springdata.v2.changes._1_create_client_collection_happy;
import io.flamingock.oss.driver.mongodb.springdata.v2.changes._2_insert_federico_happy_non_transactional;
import io.flamingock.oss.driver.mongodb.springdata.v2.changes._3_insert_jorge_happy_non_transactional;
import io.flamingock.oss.driver.mongodb.springdata.v2.driver.SpringDataMongoV2Driver;
import io.flamingock.oss.driver.mongodb.springdata.v2.mongock.ClientInitializerChangeUnit;
import io.mongock.driver.mongodb.v3.driver.MongoCore3Driver;
import io.mongock.runner.standalone.MongockStandalone;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.flamingock.core.configurator.core.CoreConfiguration.ImporterConfiguration.withSource;
import static io.flamingock.oss.driver.common.mongodb.MongoDBDriverConfiguration.DEFAULT_LOCK_REPOSITORY_NAME;
import static io.flamingock.oss.driver.common.mongodb.MongoDBDriverConfiguration.DEFAULT_MIGRATION_REPOSITORY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
class MongoSpringDataV2ImporterTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
    private static final String DB_NAME = "test";
    private static MongoClient mongoClient;
    private static MongoTemplate mongoTemplate;
    private static MongoDBTestHelper mongoDBTestHelper;

    @BeforeAll
    static void beforeAll() {
        mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        mongoTemplate = new MongoTemplate(mongoClient, DB_NAME);
        mongoDBTestHelper = new MongoDBTestHelper(mongoTemplate.getDb());
    }

    @BeforeEach
    void setupEach() {
        mongoTemplate.getCollection(DEFAULT_MIGRATION_REPOSITORY_NAME).drop();
        mongoTemplate.getCollection(DEFAULT_LOCK_REPOSITORY_NAME).drop();
    }

    @Test
    @DisplayName("When standalone runs the driver with mongock importer should run migration")
    void shouldRunMongockImporter() {
        //Given
        MongoCore3Driver mongo3Driver = MongoCore3Driver.withDefaultLock(mongoClient, DB_NAME);
        MongockStandalone.builder()
                .setDriver(mongo3Driver)
                .addMigrationClass(_0_mongock_create_authors_collection.class)
                .addDependency(mongoTemplate)
                .setTrackIgnored(true)
                .setTransactional(false)
                .buildRunner()
                .execute();

        ArrayList<Document> mongockDbState = mongoClient.getDatabase(DB_NAME).getCollection(mongo3Driver.getMigrationRepositoryName())
                .find()
                .into(new ArrayList<>());

        Assertions.assertEquals(4, mongockDbState.size());
        assertEquals("system-change-00001_before", mongockDbState.get(0).get("changeId"));
        assertEquals("system-change-00001", mongockDbState.get(1).get("changeId"));
        assertEquals("create-author-collection_before", mongockDbState.get(2).get("changeId"));
        assertEquals("create-author-collection", mongockDbState.get(3).get("changeId"));

        //When
        try (MockedStatic<Deserializer> mocked = Mockito.mockStatic(Deserializer.class)) {
            mocked.when(Deserializer::readPreviewPipelineFromFile).thenReturn(mongoDBTestHelper.getPreviewPipeline(
                    new Trio<>(_0_mongock_create_authors_collection.class, Collections.singletonList(MongoTemplate.class)),
                    new Trio<>(_1_create_client_collection_happy.class, Collections.singletonList(MongoTemplate.class)),
                    new Trio<>(_2_insert_federico_happy_non_transactional.class, Collections.singletonList(MongoTemplate.class)),
                    new Trio<>(_3_insert_jorge_happy_non_transactional.class, Collections.singletonList(MongoTemplate.class), Collections.singletonList(MongoTemplate.class)))
            );

            FlamingockStandalone.local()
                    .setDriver(new SpringDataMongoV2Driver(mongoTemplate))
                    .withImporter(withSource(mongo3Driver.getMigrationRepositoryName()))
                    //.addStage(new Stage("stage-name").addCodePackage("io.flamingock.oss.driver.mongodb.springdata.v2.changes.happyPathWithTransaction"))
                    .addDependency(mongoTemplate)
                    .disableTransaction()
                    .build()
                    .run();
        }

        List<AuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(DEFAULT_MIGRATION_REPOSITORY_NAME);
        assertEquals(8, auditLog.size());
        checkAuditEntry(
                auditLog.get(0),
                mongockDbState.get(0).getString("executionId"),
                MongockLegacyIdGenerator.getNewId(mongockDbState.get(0).getString("changeId"), mongockDbState.get(0).getString("author")),
                AuditEntry.Status.EXECUTED,
                mongockDbState.get(0).getString("changeLogClass"),
                mongockDbState.get(0).getString("changeSetMethod"),
                AuditEntry.ExecutionType.BEFORE_EXECUTION,
                true);
        checkAuditEntry(
                auditLog.get(1),
                mongockDbState.get(1).getString("executionId"),
                MongockLegacyIdGenerator.getNewId(mongockDbState.get(1).getString("changeId"), mongockDbState.get(1).getString("author")),
                AuditEntry.Status.EXECUTED,
                mongockDbState.get(1).getString("changeLogClass"),
                mongockDbState.get(1).getString("changeSetMethod"),
                AuditEntry.ExecutionType.EXECUTION,
                true);

        checkAuditEntry(
                auditLog.get(2),
                mongockDbState.get(2).getString("executionId"),
                MongockLegacyIdGenerator.getNewId(mongockDbState.get(2).getString("changeId"), mongockDbState.get(2).getString("author")),
                AuditEntry.Status.EXECUTED,
                mongockDbState.get(2).getString("changeLogClass"),
                mongockDbState.get(2).getString("changeSetMethod"),
                AuditEntry.ExecutionType.BEFORE_EXECUTION,
                false);

        checkAuditEntry(
                auditLog.get(3),
                mongockDbState.get(3).getString("executionId"),
                MongockLegacyIdGenerator.getNewId(mongockDbState.get(3).getString("changeId"), mongockDbState.get(3).getString("author")),
                AuditEntry.Status.EXECUTED,
                mongockDbState.get(3).getString("changeLogClass"),
                mongockDbState.get(3).getString("changeSetMethod"),
                AuditEntry.ExecutionType.EXECUTION,
                false);

        checkAuditEntry(
                auditLog.get(4),
                auditLog.get(4).getExecutionId(),
                MongockImporterChangeUnit.IMPORTER_FROM_MONGOCK,
                AuditEntry.Status.EXECUTED,
                MongockImporterChangeUnit.class.getName(),
                "execution",
                AuditEntry.ExecutionType.EXECUTION,
                false);


    }

    private void checkAuditEntry(AuditEntry actualAuditEntry,
                                 String expectedExecutionId,
                                 String expectedTaskId,
                                 AuditEntry.Status expectedStatus,
                                 String expectedClassName,
                                 String expectedMethodName,
                                 AuditEntry.ExecutionType expectedExecutionType,
                                 boolean expectedSystemChange) {
        assertEquals(expectedExecutionId, actualAuditEntry.getExecutionId());
        assertEquals(expectedTaskId, actualAuditEntry.getTaskId());
        assertEquals(expectedStatus, actualAuditEntry.getState());
        assertEquals(expectedClassName, actualAuditEntry.getClassName());
        assertEquals(expectedMethodName, actualAuditEntry.getMethodName());
        assertEquals(expectedExecutionType, actualAuditEntry.getType());
        assertEquals(expectedSystemChange, actualAuditEntry.getSystemChange());

    }

}