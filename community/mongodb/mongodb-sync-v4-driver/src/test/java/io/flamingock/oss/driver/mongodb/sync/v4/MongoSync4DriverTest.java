package io.flamingock.oss.driver.mongodb.sync.v4;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.commuinty.runner.standalone.CommunityStandalone;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MongoSync4DriverTest {

    private static final String DB_NAME = "test";

    private static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";

    private static final String CLIENTS_COLLECTION = "clientCollection";

    private static MongoClient mongoClient;

    private static MongoDatabase mongoDatabase;

    private static MongoDBTestHelper mongoDBTestHelper;


    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @BeforeAll
    static void beforeAll() {
        mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
        mongoDBTestHelper = new MongoDBTestHelper(mongoDatabase);
    }

    @BeforeEach
    void setupEach() {
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).deleteMany(new Document());
    }

    @AfterEach
    void tearDownEach() {
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

    @Test
    @DisplayName("When standalone runs the driver should persist the audit logs and the user's collection updated")
    void happyPath() {
        //Given-When
        CommunityStandalone.builder()
                .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, DB_NAME))
                .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.sync.v4.changes")
                .addDependency(mongoClient.getDatabase(DB_NAME))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .build()
                .run();

        //Then
        //Checking auditLog
        List<String> auditLog = mongoDBTestHelper.getAuditLogSorted(AUDIT_LOG_COLLECTION);
        assertEquals(3, auditLog.size());
        assertEquals("create-collection", auditLog.get(0));
        assertEquals("insert-document", auditLog.get(1));
        assertEquals("insert-another-document", auditLog.get(2));

        //Checking clients collection
        Set<String> clients = mongoDatabase.getCollection(CLIENTS_COLLECTION)
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());
        assertEquals(2, clients.size());
        assertTrue(clients.contains("Federico"));
        assertTrue(clients.contains("Jorge"));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }
}