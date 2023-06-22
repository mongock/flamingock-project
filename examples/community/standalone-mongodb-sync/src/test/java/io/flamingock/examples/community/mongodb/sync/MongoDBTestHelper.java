package io.flamingock.examples.community.mongodb.sync;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.flamingock.core.core.util.TimeUtil;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_TIMESTAMP;

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
