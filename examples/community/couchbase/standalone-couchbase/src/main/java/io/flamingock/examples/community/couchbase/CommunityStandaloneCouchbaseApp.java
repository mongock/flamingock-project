package io.flamingock.examples.community.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;

import io.flamingock.community.runner.standalone.CommunityStandalone;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.examples.community.couchbase.events.FailureEventListener;
import io.flamingock.examples.community.couchbase.events.StartedEventListener;
import io.flamingock.examples.community.couchbase.events.SuccessEventListener;
import io.flamingock.oss.driver.couchbase.driver.CouchbaseDriver;

public class CommunityStandaloneCouchbaseApp {

    private static final String BUCKET_NAME = "bucket";

    public static void main(String[] args) {
        new CommunityStandaloneCouchbaseApp().run(connect(), BUCKET_NAME);
    }

    public void run(Cluster cluster, String bucketName) {
        Collection collection = cluster.bucket(bucketName).defaultCollection();
        CommunityStandalone.builder()
                .setDriver(new CouchbaseDriver(cluster, collection))
                .setLockAcquiredForMillis(60 * 1000L)// this is just to show how is set. Default value is still 60 * 1000L
                .setLockQuitTryingAfterMillis(3 * 60 * 1000L)// this is just to show how is set. Default value is still 3 * 60 * 1000L
                .setLockTryFrequencyMillis(1000L)// this is just to show how is set. Default value is still 1000L
                .addStage(new Stage().addCodePackage("io.flamingock.examples.community.couchbase.changes"))
                .addDependency(cluster)
                .addDependency(collection)
                .setTrackIgnored(true)
                .setTransactionEnabled(false)
                .setPipelineStartedListener(new StartedEventListener())
                .setPipelineCompletedListener(new SuccessEventListener())
                .setPipelineFailureListener(new FailureEventListener())
                .build()
                .run();
    }

    private static Cluster connect() {
        return Cluster.connect("couchbase://localhost:11210",
                "Administrator",
                "password");
    }
}