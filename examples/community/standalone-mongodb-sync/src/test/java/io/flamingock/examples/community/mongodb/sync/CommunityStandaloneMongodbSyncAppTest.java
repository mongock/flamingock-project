package io.flamingock.examples.community.mongodb.sync;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.examples.community.mongodb.sync.changes.ACreateCollection;
import io.flamingock.examples.community.mongodb.sync.changes.BInsertDocument;
import io.flamingock.examples.community.mongodb.sync.changes.CInsertAnotherDocument;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class CommunityStandaloneMongodbSyncAppTest {

    public static final String DB_NAME = "test";
    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";
    public static MongoClient mongoClient;

    public static MongoDatabase mongoDatabase;

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));


    @BeforeAll
    static void beforeAll() {
        mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
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
    void happyPath() {
        //Given-When
        new CommunityStandaloneMongodbSyncApp().run(mongoClient, DB_NAME);
        assertEquals(3, ChangesTracker.changes.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));
    }

}
