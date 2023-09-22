package io.flamingock.examples.community.couchbase;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.couchbase.client.java.Cluster;

import io.flamingock.examples.community.couchbase.changes.IndexInitializerChangeUnit;
import io.flamingock.examples.community.couchbase.events.FailureEventListener;
import io.flamingock.examples.community.couchbase.events.StartedEventListener;
import io.flamingock.examples.community.couchbase.events.SuccessEventListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class SuccessExecutionTest {

    private static final String BUCKET_NAME = "bucket";

    @Container
    public static final CouchbaseContainer container = new CouchbaseContainer("couchbase/server").withBucket(new BucketDefinition(BUCKET_NAME));


    @BeforeAll
    static void beforeAll() {
        Cluster cluster = Cluster.connect(container.getConnectionString(), container.getUsername(), container.getPassword());
        new CommunityStandaloneCouchbaseApp().run(cluster, BUCKET_NAME);
    }


    @Test
    @DisplayName("SHOULD execute all the changes WHEN executed IF happy path")
    void allChangeExecuted() {
        assertEquals(2, ChangesTracker.changes.size());
        assertEquals(IndexInitializerChangeUnit.class.getName() + "_beforeExecution", ChangesTracker.changes.get(0));
        assertEquals(IndexInitializerChangeUnit.class.getName() + "_execution", ChangesTracker.changes.get(1));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(StartedEventListener.executed);
        assertTrue(SuccessEventListener.executed);
        assertFalse(FailureEventListener.executed);
    }

}
