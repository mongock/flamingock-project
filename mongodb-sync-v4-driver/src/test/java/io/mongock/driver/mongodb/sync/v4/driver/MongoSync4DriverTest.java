package io.mongock.driver.mongodb.sync.v4.driver;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.mongock.runner.standalone.MongockStandalone;
import org.junit.jupiter.api.Test;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

class MongoSync4DriverTest {

    public final static String MONGODB_CONNECTION_STRING = "mongodb://localhost:27017/";
    public final static String MONGODB_MAIN_DB_NAME = "test";

    @Test
    void test1() {
        MongockStandalone.builder()
                .setDriver(MongoSync4Driver.withDefaultLock(getMainMongoClient(), MONGODB_MAIN_DB_NAME))
                .addMigrationScanPackage("io.mongock.driver.mongodb.sync.v4.driver.changes")
//                .setMigrationStartedListener(MongockEventListener::onStart)
//                .setMigrationSuccessListener(MongockEventListener::onSuccess)
//                .setMigrationFailureListener(MongockEventListener::onFail)
                .addDependency(new MyDependency())
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .build()
                .run();
    }


    /**
     * Main MongoClient for Mongock to work.
     */
    private static MongoClient getMainMongoClient() {
        return buildMongoClientWithCodecs(MONGODB_CONNECTION_STRING);
    }


    /**
     * Helper to create MongoClients customized including Codecs
     */
    private static MongoClient buildMongoClientWithCodecs(String connectionString) {

//        CodecRegistry codecRegistry = fromRegistries(CodecRegistries.fromCodecs(new ZonedDateTimeCodec()),
//                MongoClientSettings.getDefaultCodecRegistry(),
//                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
//        builder.codecRegistry(codecRegistry);
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }

}