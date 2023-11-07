package io.flamingock.examples.community;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.flamingock.core.configurator.standalone.FlamingockStandalone;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.examples.community.events.PipelineFailedListener;
import io.flamingock.examples.community.events.PipelineStartedListener;
import io.flamingock.examples.community.events.PipelineCompletedListener;
import io.flamingock.examples.community.events.StageCompletedListener;
import io.flamingock.examples.community.events.StageFailedListener;
import io.flamingock.examples.community.events.StageStartedListener;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class CommunityStandaloneMongodbSyncApp {


    public final static String DATABASE_NAME = "test";
    public static void main(String[] args) {
        new CommunityStandaloneMongodbSyncApp()
                .run(getMongoClient("mongodb://localhost:27017/"), DATABASE_NAME);
    }


    public  void run(MongoClient mongoClient, String databaseName) {
        FlamingockStandalone.local()
                .setDriver(new MongoSync4Driver(mongoClient, databaseName))
                .setLockAcquiredForMillis(60 * 1000L)//this is just to show how is set. Default value is still 60 * 1000L
                .setLockQuitTryingAfterMillis(3 * 60 * 1000L)//this is just to show how is set. Default value is still 3 * 60 * 1000L
                .setLockTryFrequencyMillis(1000L)//this is just to show how is set. Default value is still 1000L
                .addStage(new Stage().addCodePackage("io.flamingock.examples.community.changes"))
                .addDependency(mongoClient.getDatabase(databaseName))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .setPipelineStartedListener(new PipelineStartedListener())
                .setPipelineCompletedListener(new PipelineCompletedListener())
                .setPipelineFailedListener(new PipelineFailedListener())
                .setStageStartedListener(new StageStartedListener())
                .setStageCompletedListener(new StageCompletedListener())
                .setStageFailedListener(new StageFailedListener())
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