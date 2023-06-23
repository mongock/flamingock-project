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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import({CommunitySpringbootMongodbSyncAppTest.TestConfiguration.class, CommunitySpringbootMongodbSyncApp.class})
class CommunitySpringbootMongodbSyncAppTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Autowired
    private MongoDatabase mongoDatabase;

    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection("mongockChangeLog").deleteMany(new Document());
    }

    @Test
    void happyPath() {
        assertEquals(3, ChangesTracker.changes.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));

        //tear-down
        mongoDatabase.getCollection("clientCollection").drop();
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