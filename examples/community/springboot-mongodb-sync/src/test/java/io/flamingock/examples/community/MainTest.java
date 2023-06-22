package io.flamingock.examples.community;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.flamingock.examples.community.changes.ACreateCollection;
import io.flamingock.examples.community.changes.BInsertDocument;
import io.flamingock.examples.community.changes.CInsertAnotherDocument;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.flamingock.examples.community.MongoDBTestHelper.mongoClient;
import static io.flamingock.examples.community.MongoDBTestHelper.mongoDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(Main.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class MainTest {

    @Configuration
    static class TestConfiguration {
        @Bean
        @Primary
        public MongoClient mongoClient() {
            return mongoClient;
        }

        @Bean
        @Primary
        public MongoDatabase mongoDatabase() {
            return mongoDatabase;
        }

    }

    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";


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

}