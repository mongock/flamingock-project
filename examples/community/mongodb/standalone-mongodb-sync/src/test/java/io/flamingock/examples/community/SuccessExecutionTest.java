package io.flamingock.examples.community;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.CompletedEventListener;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static io.flamingock.oss.driver.common.mongodb.MongoDBDriverConfiguration.LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
import static io.flamingock.examples.community.CommunityStandaloneMongodbSyncApp.DATABASE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class SuccessExecutionTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    private static MongoClient mongoClient;

    @BeforeAll
    static void beforeAll() {
        mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        new CommunityStandaloneMongodbSyncApp().run(mongoClient, "test");
    }


    @Test
    @DisplayName("SHOULD create clientCollection and insert two clients")
    void functionalTest() {
        Set<String> clients = mongoClient.getDatabase(DATABASE_NAME)
                .getCollection("clientCollection")
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());

        assertTrue(clients.contains("Jorge"));
        assertTrue(clients.contains("Federico"));
        assertEquals(2, clients.size());
    }

    @Test
    @DisplayName("SHOULD insert the Flamingock change history")
    void flamingockLogsTest() {
        ArrayList<Document> flamingockDocuments = mongoClient.getDatabase(DATABASE_NAME)
                .getCollection(LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME)
                .find()
                .into(new ArrayList<>());

        Document aCreateCollection = flamingockDocuments.get(0);
        assertEquals("create-collection", aCreateCollection.get("changeId"));
        assertEquals("EXECUTED", aCreateCollection.get("state"));
        assertEquals("io.flamingock.examples.community.changes.ACreateCollection", aCreateCollection.get("changeLogClass"));

        Document bInsertDocument = flamingockDocuments.get(1);
        assertEquals("insert-document", bInsertDocument.get("changeId"));
        assertEquals("EXECUTED", bInsertDocument.get("state"));
        assertEquals("io.flamingock.examples.community.changes.BInsertDocument", bInsertDocument.get("changeLogClass"));

        Document cInsertAnotherDocument = flamingockDocuments.get(2);
        assertEquals("insert-another-document", cInsertAnotherDocument.get("changeId"));
        assertEquals("EXECUTED", cInsertAnotherDocument.get("state"));
        assertEquals("io.flamingock.examples.community.changes.CInsertAnotherDocument", cInsertAnotherDocument.get("changeLogClass"));

        assertEquals(3, flamingockDocuments.size());
    }



    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(StartedEventListener.executed);
        assertTrue(CompletedEventListener.executed);
        assertFalse(FailureEventListener.executed);
    }

}
