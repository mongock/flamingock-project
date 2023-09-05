package io.flamingock.examples.community.mongodb.sync;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.flamingock.community.runner.standalone.CommunityStandalone;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.examples.community.mongodb.sync.events.FailureEventListener;
import io.flamingock.examples.community.mongodb.sync.events.StartedEventListener;
import io.flamingock.examples.community.mongodb.sync.events.SuccessEventListener;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class CommunityStandaloneMongodbSyncApp {
    public static void main(String[] args) {
        new CommunityStandaloneMongodbSyncApp()
                .run(getMongoClient("mongodb://localhost:27017/"), "test");
    }


    public  void run(MongoClient mongoClient, String databaseName) {
        CommunityStandalone.builder()
                .setDriver(new MongoSync4Driver(mongoClient, databaseName))
                .setLockAcquiredForMillis(60 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                .setLockQuitTryingAfterMillis(3 * 60 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                .setLockTryFrequencyMillis(1000L)//this is just to show how is set. Default value is still 1000L
                .addStage(new Stage("io.flamingock.examples.community.mongodb.sync.changes"))
                .addDependency(mongoClient.getDatabase(databaseName))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .setMigrationStartedListener(new StartedEventListener())
                .setMigrationSuccessListener(new SuccessEventListener())
                .setMigrationFailureListener(new FailureEventListener())
                .build()
                .run();
    }

    private static MongoClient getMongoClient(String connectionString) {

        CodecRegistry codecRegistry = fromRegistries(CodecRegistries.fromCodecs(new ZonedDateTimeCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        builder.codecRegistry(codecRegistry);
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }
}