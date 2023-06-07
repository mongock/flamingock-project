package io.flamingock.oss.driver.mongodb.sync.v4;

import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.flamingock.oss.runner.standalone.MongockStandalone;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.flamingock.oss.driver.mongodb.sync.v4.MongoDBTestHelper.getAuditLogSorted;
import static io.flamingock.oss.driver.mongodb.sync.v4.MongoDBTestHelper.mongoClient;
import static io.flamingock.oss.driver.mongodb.sync.v4.MongoDBTestHelper.mongoDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoSync4DriverTest {

    public static final String DB_NAME = "test";
    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";


    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).deleteMany(new Document());
    }


    @Test
    @DisplayName("When standalone runs the driver should persist the audit logs and the user's collection updated")
    void happyPath() {
        //Given-When
        MongockStandalone.builder()
                .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, DB_NAME))
                .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.sync.v4.changes")
                .addDependency(mongoClient.getDatabase(DB_NAME))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .build()
                .run();

        //Then
        //Checking auditLog
        List<String> auditLog = getAuditLogSorted(AUDIT_LOG_COLLECTION);
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