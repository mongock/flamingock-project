package io.flamingock.oss.driver.mongodb.v3;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.commuinty.runner.standalone.CommunityStandalone;
import io.flamingock.community.internal.persistence.MongockAuditEntry;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.execution.executor.ProcessExecutionException;
import io.flamingock.oss.driver.mongodb.v3.driver.Mongo3Driver;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class Mongo3DriverTest {

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
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).drop();
    }

    @AfterEach
    void tearDownEach() {
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

    @Test
    @DisplayName("When standalone runs the driver with transactions enabled should persist the audit logs and the user's collection updated")
    void happyPathWithTransaction() {
        //Given-When
        CommunityStandalone.builder()
                .setDriver(new Mongo3Driver(mongoClient, DB_NAME))
                .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.v3.changes.happyPathWithTransaction")
                .addDependency(mongoClient.getDatabase(DB_NAME))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .build()
                .run();

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(AUDIT_LOG_COLLECTION);
        assertEquals(3, auditLog.size());
        assertEquals("create-collection", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("insert-another-document", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(2).getState());

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

    @Test
    @DisplayName("When standalone runs the driver with transactions disabled should persist the audit logs and the user's collection updated")
    void happyPathWithoutTransaction() {
        //Given-When
        CommunityStandalone.builder()
                .setDriver(new Mongo3Driver(mongoClient, DB_NAME))
                .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.v3.changes.happyPathWithoutTransaction")
                .addDependency(mongoClient.getDatabase(DB_NAME))
                .setTrackIgnored(true)
                .setTransactionEnabled(false)
                .build()
                .run();

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(AUDIT_LOG_COLLECTION);
        assertEquals(3, auditLog.size());
        assertEquals("create-collection", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("insert-another-document", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(2).getState());

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

    @Test
    @DisplayName("When standalone runs the driver with transactions enabled and execution fails should persist only the executed audit logs")
    void failedWithTransaction() {
        //Given-When
        assertThrows(ProcessExecutionException.class, () -> {
            CommunityStandalone.builder()
                    .setDriver(new Mongo3Driver(mongoClient, DB_NAME))
                    .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.v3.changes.failedWithTransaction")
                    .addDependency(mongoClient.getDatabase(DB_NAME))
                    .setTrackIgnored(true)
                    .setTransactionEnabled(true)
                    .build()
                    .run();
        });

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(AUDIT_LOG_COLLECTION);
        assertEquals(2, auditLog.size());
        assertEquals("create-collection", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());

        //Checking clients collection
        Set<String> clients = mongoDatabase.getCollection(CLIENTS_COLLECTION)
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());
        assertEquals(1, clients.size());
        assertTrue(clients.contains("Federico"));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

    @Test
    @DisplayName("When standalone runs the driver with transactions disabled and execution fails (with rollback method) should persist all the audit logs up to the failed one (ROLLED_BACK)")
    void failedWithoutTransactionWithRollback() {
        //Given-When
        assertThrows(ProcessExecutionException.class, () -> {
            CommunityStandalone.builder()
                    .setDriver(new Mongo3Driver(mongoClient, DB_NAME))
                    .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.v3.changes.failedWithoutTransactionWithRollback")
                    .addDependency(mongoClient.getDatabase(DB_NAME))
                    .setTrackIgnored(true)
                    .setTransactionEnabled(false)
                    .build()
                    .run();
        });

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(AUDIT_LOG_COLLECTION);
        assertEquals(3, auditLog.size());
        assertEquals("create-collection", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("execution-with-exception", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.ROLLED_BACK, auditLog.get(2).getState());

        //Checking clients collection
        Set<String> clients = mongoDatabase.getCollection(CLIENTS_COLLECTION)
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());
        assertEquals(1, clients.size());
        assertTrue(clients.contains("Federico"));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

    @Test
    @DisplayName("When standalone runs the driver with transactions disabled and execution fails (without rollback method) should persist all the audit logs up to the failed one (FAILED)")
    void failedWithoutTransactionWithoutRollback() {
        //Given-When
        assertThrows(ProcessExecutionException.class, () -> {
            CommunityStandalone.builder()
                    .setDriver(new Mongo3Driver(mongoClient, DB_NAME))
                    .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.v3.changes.failedWithoutTransactionWithoutRollback")
                    .addDependency(mongoClient.getDatabase(DB_NAME))
                    .setTrackIgnored(true)
                    .setTransactionEnabled(false)
                    .build()
                    .run();
        });

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = mongoDBTestHelper.getAuditEntriesSorted(AUDIT_LOG_COLLECTION);
        assertEquals(3, auditLog.size());
        assertEquals("create-collection", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("execution-with-exception", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.FAILED, auditLog.get(2).getState());

        //Checking clients collection
        Set<String> clients = mongoDatabase.getCollection(CLIENTS_COLLECTION)
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());
        assertEquals(1, clients.size());
        assertTrue(clients.contains("Federico"));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }
}