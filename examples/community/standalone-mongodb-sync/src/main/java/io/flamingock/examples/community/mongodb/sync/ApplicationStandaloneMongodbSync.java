package io.flamingock.examples.community.mongodb.sync;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.commuinty.runner.standalone.CommunityStandalone;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ApplicationStandaloneMongodbSync {
    public static void main(String[] args) {
        new ApplicationStandaloneMongodbSync()
                .run(getMongoClient("mongodb://localhost:27017/"), "test");
    }


    public  void run(MongoClient mongoClient, String databaseName) {
        CommunityStandalone.builder()
                .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, databaseName))
                .addMigrationScanPackage("io.flamingock.examples.community.mongodb.sync.changes")
                .addDependency(mongoClient.getDatabase(databaseName))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
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