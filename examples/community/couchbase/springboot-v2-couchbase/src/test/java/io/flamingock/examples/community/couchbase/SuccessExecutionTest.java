package io.flamingock.examples.community.couchbase;

import com.couchbase.client.core.io.CollectionIdentifier;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.flamingock.examples.community.couchbase.config.CouchbaseInitializer;
import io.flamingock.examples.community.couchbase.events.FailureEventListener;
import io.flamingock.examples.community.couchbase.events.StartedEventListener;
import io.flamingock.examples.community.couchbase.events.SuccessEventListener;
import io.flamingock.oss.driver.couchbase.internal.util.N1QLQueryProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_TIMESTAMP;
import static io.flamingock.examples.community.couchbase.config.CouchbaseInitializer.BUCKET_NAME;
import static io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants.DOCUMENT_TYPE_AUDIT_ENTRY;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import({CommunitySpringbootV2CouchbaseApp.class})
@ContextConfiguration(initializers = CouchbaseInitializer.class)
class SuccessExecutionTest {

    @Autowired
    private StartedEventListener startedEventListener;

    @Autowired
    private SuccessEventListener successEventListener;

    @Autowired
    private FailureEventListener failureEventListener;

    @Autowired
    private Cluster cluster;

    @Test
    @DisplayName("SHOULD create idx_springboot_index index")
    void functionalTest() {
        List<QueryIndex> indexes = cluster
                .queryIndexes()
                .getAllIndexes(BUCKET_NAME)
                .stream()
                .filter(i -> i.name().equals("idx_springboot_index"))
                .collect(Collectors.toList());

        assertEquals(1, indexes.size());
    }

    @Test
    @DisplayName("SHOULD insert the Flamingock change history")
    void flamingockLogsTest() {
        QueryResult result = cluster.query(
                N1QLQueryProvider.selectAllChangesQuery(BUCKET_NAME, CollectionIdentifier.DEFAULT_SCOPE, CollectionIdentifier.DEFAULT_COLLECTION),
                QueryOptions.queryOptions().parameters(JsonObject.create().put("p", DOCUMENT_TYPE_AUDIT_ENTRY))
                        .scanConsistency(QueryScanConsistency.REQUEST_PLUS));

        List<JsonObject> flamingockDocuments = result
                .rowsAsObject()
                .stream()
                .sorted(Comparator.comparing(o -> o.getLong(KEY_TIMESTAMP)))
                .collect(Collectors.toList());

        JsonObject beforeExecutionEntry = flamingockDocuments.get(0);
        assertEquals("index-initializer_before", beforeExecutionEntry.get("changeId"));
        assertEquals("EXECUTED", beforeExecutionEntry.get("state"));
        assertEquals("io.flamingock.examples.community.couchbase.changes.IndexInitializerChangeUnit", beforeExecutionEntry.get("changeLogClass"));

        JsonObject executionEntry = flamingockDocuments.get(1);
        assertEquals("index-initializer", executionEntry.get("changeId"));
        assertEquals("EXECUTED", executionEntry.get("state"));
        assertEquals("io.flamingock.examples.community.couchbase.changes.IndexInitializerChangeUnit", executionEntry.get("changeLogClass"));

        assertEquals(2, flamingockDocuments.size());
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startedEventListener.executed);
        assertTrue(successEventListener.executed);
        assertFalse(failureEventListener.executed);
    }
}