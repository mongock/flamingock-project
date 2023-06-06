package io.flamingock.oss.driver.mongodb.sync.v4;


//import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
//import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.oss.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.flamingock.oss.runner.standalone.MongockStandalone;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

class MongoSync4DriverTest {

//    public final static String MONGODB_CONNECTION_STRING = "mongodb://localhost:27017/";
    public final static String MONGODB_MAIN_DB_NAME = "test";

    private final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));


    @Test
    void test1() {
        mongoDBContainer.start();
        MongoClient mongoClient = getMainMongoClient();

        MongockStandalone.builder()
                .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, MONGODB_MAIN_DB_NAME))
                .addMigrationScanPackage("io.flamingock.oss.driver.mongodb.sync.v4.changes")
                .addDependency(mongoClient.getDatabase(MONGODB_MAIN_DB_NAME))
                .setTrackIgnored(true)
                .setTransactionEnabled(true)
                .build()
                .run();

    }


    /**
     * Main MongoClient for Mongock to work.
     */
    private MongoClient getMainMongoClient() {
        return buildMongoClientWithCodecs(mongoDBContainer.getConnectionString());
    }


    /**
     * Helper to create MongoClients customized including Codecs
     */
    private MongoClient buildMongoClientWithCodecs(String connectionString) {

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