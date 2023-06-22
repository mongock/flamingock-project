package io.flamingock.examples.community;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDBTestHelper {

    public static final String DB_NAME = "test";
    public static final MongoDBContainer mongoDBContainer;
    public static final MongoClient mongoClient;
    public static final MongoDatabase mongoDatabase;

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
        mongoDBContainer.start();
        mongoClient = getMainMongoClient(mongoDBContainer.getConnectionString());
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    private static MongoClient getMainMongoClient(String connectionString) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }
}
