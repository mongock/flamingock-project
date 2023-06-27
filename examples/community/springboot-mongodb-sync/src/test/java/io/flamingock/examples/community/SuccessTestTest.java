package io.flamingock.examples.community;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.examples.community.changes.ACreateCollection;
import io.flamingock.examples.community.changes.BInsertDocument;
import io.flamingock.examples.community.changes.CInsertAnotherDocument;
<<<<<<<< HEAD:examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessExecutionTest.java
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.SuccessEventListener;
========
import io.flamingock.examples.community.events.FailedFlamingockListener;
import io.flamingock.examples.community.events.StartFlamingockListener;
import io.flamingock.examples.community.events.SuccessFlamingockListener;
>>>>>>>> d117996 (added spring events to exampels):examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessTestTest.java
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
<<<<<<<< HEAD:examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessExecutionTest.java
@Import({SuccessExecutionTest.TestConfiguration.class, CommunitySpringbootMongodbSyncApp.class})
class SuccessExecutionTest {
========
@Import({SuccessTestTest.TestConfiguration.class, CommunitySpringbootMongodbSyncApp.class})
class SuccessTestTest {
>>>>>>>> d117996 (added spring events to exampels):examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessTestTest.java

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Autowired
    private StartedEventListener startedEventListener;

    @Autowired
<<<<<<<< HEAD:examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessExecutionTest.java
    private SuccessEventListener successEventListener;

    @Autowired
    private FailureEventListener failureEventListener;


    @Test
    @DisplayName("SHOULD execute all the changes WHEN executed IF happy path")
    void allChangeExecuted() {
        assertEquals(3, ChangesTracker.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.get(2));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startedEventListener.executed);
        assertTrue(successEventListener.executed);
        assertFalse(failureEventListener.executed);
========
    private StartFlamingockListener startFlamingockListener;

    @Autowired
    private SuccessFlamingockListener successFlamingockListener;

    @Autowired
    private FailedFlamingockListener failedFlamingockListener;



    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection("mongockChangeLog").deleteMany(new Document());
        mongoDatabase.getCollection("clientCollection").drop();
    }

    @Test
    @DisplayName("SHOULD execute all the changes WHEN executed IF happy path")
    void happyPath() {
        assertEquals(3, ChangesTracker.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.get(2));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startFlamingockListener.executed);
        assertTrue(successFlamingockListener.executed);
        assertFalse(failedFlamingockListener.executed);
>>>>>>>> d117996 (added spring events to exampels):examples/community/springboot-mongodb-sync/src/test/java/io/flamingock/examples/community/SuccessTestTest.java
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        @Primary
        public MongoClient mongoClient() {
            return MongoClients.create(MongoClientSettings
                    .builder()
                    .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                    .build());
        }
    }
}