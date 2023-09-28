package io.flamingock.examples.community;


import io.flamingock.examples.community.config.MongoInitializer;
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.SuccessEventListener;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static io.flamingock.oss.driver.common.mongodb.MongoDBDriverConfiguration.LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME;
import static io.flamingock.examples.community.CommunitySpringbootMongodbSpringdataApp.CLIENTS_COLLECTION_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Import({CommunitySpringbootMongodbSpringdataApp.class})
@ContextConfiguration(initializers = MongoInitializer.class)
class SpringData3SuccessExecutionTest {

    @Autowired
    private StartedEventListener startedEventListener;

    @Autowired
    private SuccessEventListener successEventListener;

    @Autowired
    private FailureEventListener failureEventListener;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    @DisplayName("SHOULD create clientCollection and insert two clients")
    void functionalTest() {
        Set<String> clients = mongoTemplate.getCollection(CLIENTS_COLLECTION_NAME)
                .find()
                .map(document -> document.getString("name"))
                .into(new HashSet<>());

        assertTrue(clients.contains("Jorge"));
        assertTrue(clients.contains("Federico"));
        assertEquals(2, clients.size());
    }

    @Test
    @DisplayName("SHOULD insert the Flamingock change history for all, code and templated changeUnits")
    void flamingockLogsTest() {
        ArrayList<Document> flamingockDocuments = mongoTemplate.getCollection(LEGACY_DEFAULT_MIGRATION_REPOSITORY_NAME)
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

        Document createCollectionTemplate = flamingockDocuments.get(3);
        assertEquals("create-collection-from-template", createCollectionTemplate.get("changeId"));
        assertEquals("EXECUTED", createCollectionTemplate.get("state"));
        assertEquals("com.company.template.mongodb.MongoCreateCollectionTemplate", createCollectionTemplate.get("changeLogClass"));

        assertEquals(4, flamingockDocuments.size());
    }


    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startedEventListener.executed);
        assertTrue(successEventListener.executed);
        assertFalse(failureEventListener.executed);
    }
}