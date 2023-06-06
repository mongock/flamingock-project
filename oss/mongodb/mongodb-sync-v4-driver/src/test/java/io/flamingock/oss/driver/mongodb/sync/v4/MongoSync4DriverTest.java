package io.flamingock.oss.driver.mongodb.sync.v4;


//import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
//import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.flamingock.oss.runner.standalone.MongockStandalone;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.flamingock.oss.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.oss.internal.persistence.AuditEntryField.KEY_TIMESTAMP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoSync4DriverTest {

    public static final String DB_NAME = "test";
    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";


    private static MongoDBContainer mongoDBContainer;
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    @BeforeAll
    static void setupAll() {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
        mongoDBContainer.start();
        mongoClient = getMainMongoClient(mongoDBContainer.getConnectionString());
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDownAll() {
        mongoDBContainer.stop();
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
        List<String> auditLog = getAuditLogSorted(mongoDatabase);
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

    private static List<String> getAuditLogSorted(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(AUDIT_LOG_COLLECTION)
                .find()
                .into(new LinkedList<>())
                .stream()
                .sorted(Comparator.comparing(d -> TimeUtil.toLocalDateTime(d.get(KEY_TIMESTAMP))))
                .map(document -> document.getString(KEY_CHANGE_ID))
                .collect(Collectors.toList());
    }

    private static MongoClient getMainMongoClient(String connectionString) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }

}