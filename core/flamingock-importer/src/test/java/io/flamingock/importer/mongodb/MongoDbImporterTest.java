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

package io.flamingock.importer.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.flamingock.community.Flamingock;
import io.flamingock.internal.core.community.Constants;
import io.flamingock.internal.core.runner.Runner;
import io.flamingock.template.mongodb.MongoChangeTemplate;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static io.flamingock.internal.core.community.Constants.DEFAULT_AUDIT_STORE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class MongoDbImporterTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6"));

    private static final String DB_NAME = "test";
    public static final String MONGOCK_CHANGE_LOGS = "mongockChangeLogs";

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private MongoCollection<Document> changeLogCollection;
    private MongoDbMongockTestHelper mongockTestHelper;


    @BeforeAll
    static void beforeAll() {
        mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test environment...");
        mongoDatabase.getCollection(DEFAULT_AUDIT_STORE_NAME).drop();
        mongoDatabase.getCollection(DEFAULT_AUDIT_STORE_NAME).drop();
        mongoDatabase.getCollection(MONGOCK_CHANGE_LOGS).drop();

        changeLogCollection = mongoDatabase.getCollection(MONGOCK_CHANGE_LOGS);
        mongockTestHelper = new MongoDbMongockTestHelper(changeLogCollection);
        
        // Print available templates for debugging
        System.out.println("MongoDbImporterChangeTemplate class: " + 
            io.flamingock.importer.mongodb.MongoDbImporterChangeTemplate.class.getName());
    }
    
    @Test
    void testImportMongockChangeLogs() {
        System.out.println("Setting up test scenario...");
        //adds the Mongock
        mongockTestHelper.setupBasicScenario();
        System.out.println("Mongock setup complete");
        
        System.out.println("Building Flamingock...");
        Runner flamingock = Flamingock.builder()
                .addDependency(mongoClient)
                .addDependency(mongoClient.getDatabase(DB_NAME))
                .setProperty("mongodb.databaseName", DB_NAME)
                .build();
        System.out.println("Flamingock built successfully");
        
        System.out.println("Running Flamingock...");
        flamingock.run();

        List<Document> auditLog = mongoDatabase.getCollection(DEFAULT_AUDIT_STORE_NAME)
                .find()
                .into(new ArrayList<>());

        assertEquals(2, auditLog.size());
        Document createCollectionAudit = auditLog.get(0);

        assertEquals("create-users-collection-with-index", createCollectionAudit.getString("changeId"));
        assertEquals("EXECUTED", createCollectionAudit.getString("state"));
        assertEquals(MongoChangeTemplate.class.getName(), createCollectionAudit.getString(Constants.KEY_CHANGEUNIT_CLASS));

        Document seedAudit = auditLog.get(1);
        assertEquals("seed-users", seedAudit.getString("changeId"));
        assertEquals("EXECUTED", seedAudit.getString("state"));
        assertEquals(MongoChangeTemplate.class.getName(), seedAudit.getString(Constants.KEY_CHANGEUNIT_CLASS));

        List<Document> users = mongoDatabase.getCollection("users")
                .find()
                .into(new ArrayList<>());

        assertEquals(2, users.size());
        assertEquals("Admin", users.get(0).getString("name"));
        assertEquals("admin@company.com", users.get(0).getString("email"));
        assertEquals("superuser", users.get(0).getList("roles", String.class).get(0));

        assertEquals("Backup", users.get(1).getString("name"));
        assertEquals("backup@company.com", users.get(1).getString("email"));
        assertEquals("readonly", users.get(1).getList("roles", String.class).get(0));
    }

//    private void validateEntriesInserted(int expectedCount) {
//        long actualCount = changeLogCollection.countDocuments();
//        assertEquals(expectedCount, actualCount, "Expected " + expectedCount + " entries to be inserted, but found " + actualCount);
//
//        // Validate a few specific entries
//        Document systemChange = changeLogCollection.find(Filters.eq("changeId", "system-change-00001")).first();
//        assertNotNull(systemChange, "System change entry not found");
//        assertEquals("mongock", systemChange.getString("author"));
//        assertEquals(true, systemChange.getBoolean("systemChange"));
//
//        Document clientInitializer = changeLogCollection.find(Filters.eq("changeId", "client-initializer")).first();
//        assertNotNull(clientInitializer, "Client initializer entry not found");
//        assertEquals("EXECUTION", clientInitializer.getString("type"));
//        assertEquals("execution", clientInitializer.getString("changeSetMethod"));
//
//        Document secondaryDb = changeLogCollection.find(Filters.eq("changeId", "secondarydb-with-mongodatabase")).first();
//        assertNotNull(secondaryDb, "Secondary DB entry not found");
//        assertEquals(17L, secondaryDb.getLong("executionMillis"));
//    }

}