package io.flamingock.examples.community.mongodb.sync;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.examples.community.mongodb.sync.changes.ACreateCollection;
import io.flamingock.examples.community.mongodb.sync.changes.BInsertDocument;
import io.flamingock.examples.community.mongodb.sync.changes.CInsertAnotherDocument;
import io.flamingock.examples.community.mongodb.sync.events.FailureEventListener;
import io.flamingock.examples.community.mongodb.sync.events.StartedEventListener;
import io.flamingock.examples.community.mongodb.sync.events.SuccessEventListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class SuccessExecutionTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));


    @BeforeAll
    static void beforeAll() {
        MongoClient mongoClient = MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                .build());
        new CommunityStandaloneMongodbSyncApp().run(mongoClient, "test");
    }


    @Test
    @DisplayName("SHOULD execute all the changes WHEN executed IF happy path")
    void allChangeExecuted() {
        assertEquals(3, ChangesTracker.changes.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(StartedEventListener.executed);
        assertTrue(SuccessEventListener.executed);
        assertFalse(FailureEventListener.executed);
    }

}
