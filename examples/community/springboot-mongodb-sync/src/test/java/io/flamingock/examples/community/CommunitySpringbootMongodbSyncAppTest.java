package io.flamingock.examples.community;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.examples.community.changes.ACreateCollection;
import io.flamingock.examples.community.changes.BInsertDocument;
import io.flamingock.examples.community.changes.CInsertAnotherDocument;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(CommunitySpringbootMongodbSyncApp.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CommunitySpringbootMongodbSyncAppTest {


    public static final String DB_NAME = "test";
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    static {
        mongoDBContainer.start();
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

        @Bean
        @Primary
        public MongoDatabase mongoDatabase(MongoClient mongoClient) {
            return mongoClient.getDatabase(DB_NAME);
        }

    }

    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";

    @Autowired
    private MongoDatabase mongoDatabase;

    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).deleteMany(new Document());
    }


    @Test
    void happyPath() {
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

    @Test
    void happyPath2() {
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();
    }

}