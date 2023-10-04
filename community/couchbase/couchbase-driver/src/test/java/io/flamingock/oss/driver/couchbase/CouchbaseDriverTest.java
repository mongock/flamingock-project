package io.flamingock.oss.driver.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.flamingock.community.internal.persistence.MongockAuditEntry;
import io.flamingock.community.runner.standalone.CommunityStandalone;
import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.pipeline.execution.StageExecutionException;
import io.flamingock.oss.driver.couchbase.driver.CouchbaseDriver;
import org.junit.jupiter.api.*;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CouchbaseDriverTest {

    private static final String BUCKET_NAME = "bucket";

    private static final String CLIENTS_COLLECTION = "clientCollection";

    private static Cluster cluster;
    private static CouchbaseTestHelper couchbaseTestHelper;

    @Container
    public static final CouchbaseContainer container = new CouchbaseContainer("couchbase/server").withBucket(new BucketDefinition(BUCKET_NAME));

    @BeforeAll
    static void beforeAll() {
        cluster = Cluster.connect(container.getConnectionString(), container.getUsername(), container.getPassword());
        couchbaseTestHelper = new CouchbaseTestHelper(cluster);
    }

    @BeforeEach
    void setupEach() {
    }

    @AfterEach
    void tearDownEach() {
        cluster.query(String.format("DELETE FROM `%s`", BUCKET_NAME), QueryOptions.queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS));
        cluster.queryIndexes().dropIndex(BUCKET_NAME, "idx_standalone_index", DropQueryIndexOptions.dropQueryIndexOptions().ignoreIfNotExists(true));
    }

    @Test
    @DisplayName("When standalone runs the driver should persist the audit logs and the test data")
    void happyPath() {
        //Given-When
        Collection collection = cluster.bucket(BUCKET_NAME).defaultCollection();
        CommunityStandalone.builder()
                .setDriver(new CouchbaseDriver(cluster, collection))
                .addStage(new Stage().addCodePackage("io.flamingock.oss.driver.couchbase.changes.happyPath"))
                .addDependency(cluster)
                .addDependency(collection)
                .setTrackIgnored(true)
                .setTransactionEnabled(false)
                .build()
                .run();

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = couchbaseTestHelper.getAuditEntriesSorted(collection);
        assertEquals(3, auditLog.size());
        assertEquals("create-index", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("insert-another-document", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(2).getState());

        //Checking created index and documents
        assertTrue(couchbaseTestHelper.indexExists(collection.bucketName(), "idx_standalone_index"));
        JsonObject jsonObject;
        jsonObject = collection.get("test-client-Federico").contentAsObject();
        assertNotNull(jsonObject);
        assertEquals(jsonObject.get("name"), "Federico");
        jsonObject = collection.get("test-client-Jorge").contentAsObject();
        assertNotNull(jsonObject);
        assertEquals(jsonObject.get("name"), "Jorge");
    }

    @Test
    @DisplayName("When standalone runs the driver and execution fails (with rollback method) should persist all the audit logs up to the failed one (ROLLED_BACK)")
    void failedWithRollback() {
        //Given-When
        Collection collection = cluster.bucket(BUCKET_NAME).defaultCollection();
        assertThrows(StageExecutionException.class, () -> {
            CommunityStandalone.builder()
                    .setDriver(new CouchbaseDriver(cluster, collection))
                    .addStage(new Stage().addCodePackage("io.flamingock.oss.driver.couchbase.changes.failedWithRollback"))
                    .addDependency(cluster)
                    .addDependency(collection)
                    .setTrackIgnored(true)
                    .setTransactionEnabled(false)
                    .build()
                    .run();
        });

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = couchbaseTestHelper.getAuditEntriesSorted(collection);
        assertEquals(3, auditLog.size());
        assertEquals("create-index", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("execution-with-exception", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.ROLLED_BACK, auditLog.get(2).getState());

        //Checking created index and documents
        assertTrue(couchbaseTestHelper.indexExists(collection.bucketName(), "idx_standalone_index"));
        JsonObject jsonObject;
        jsonObject = collection.get("test-client-Federico").contentAsObject();
        assertNotNull(jsonObject);
        assertEquals(jsonObject.get("name"), "Federico");
        assertFalse(collection.exists("test-client-Jorge").exists());
    }

    @Test
    @DisplayName("When standalone runs the driver and execution fails (without rollback method) should persist all the audit logs up to the failed one (FAILED)")
    void failedWithoutRollback() {
        //Given-When
        Collection collection = cluster.bucket(BUCKET_NAME).defaultCollection();
        assertThrows(StageExecutionException.class, () -> {
            CommunityStandalone.builder()
                    .setDriver(new CouchbaseDriver(cluster, collection))
                    .addStage(new Stage().addCodePackage("io.flamingock.oss.driver.couchbase.changes.failedWithoutRollback"))
                    .addDependency(cluster)
                    .addDependency(collection)
                    .setTrackIgnored(true)
                    .setTransactionEnabled(false)
                    .build()
                    .run();
        });

        //Then
        //Checking auditLog
        List<MongockAuditEntry> auditLog = couchbaseTestHelper.getAuditEntriesSorted(collection);
        assertEquals(3, auditLog.size());
        assertEquals("create-index", auditLog.get(0).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(0).getState());
        assertEquals("insert-document", auditLog.get(1).getChangeId());
        assertEquals(AuditEntryStatus.EXECUTED, auditLog.get(1).getState());
        assertEquals("execution-with-exception", auditLog.get(2).getChangeId());
        assertEquals(AuditEntryStatus.FAILED, auditLog.get(2).getState());

        //Checking created index and documents
        assertTrue(couchbaseTestHelper.indexExists(collection.bucketName(), "idx_standalone_index"));
        JsonObject jsonObject;
        jsonObject = collection.get("test-client-Federico").contentAsObject();
        assertNotNull(jsonObject);
        assertEquals(jsonObject.get("name"), "Federico");
        jsonObject = collection.get("test-client-Jorge").contentAsObject();
        assertNotNull(jsonObject);
        assertEquals(jsonObject.get("name"), "Jorge");
    }
}