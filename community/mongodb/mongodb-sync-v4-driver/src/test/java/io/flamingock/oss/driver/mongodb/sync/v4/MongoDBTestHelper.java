package io.flamingock.oss.driver.mongodb.sync.v4;

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


    public static void tearDownAll() {
        mongoDBContainer.stop();
    }

    public static List<String> getAuditLogSorted(String auditLogCollection) {
        return mongoDatabase.getCollection(auditLogCollection)
                .find()
                .into(new LinkedList<>())
                .stream()
                .sorted(Comparator.comparing(d -> TimeUtil.toLocalDateTime(d.get(KEY_TIMESTAMP))))
                .map(document -> document.getString(KEY_CHANGE_ID))
                .collect(Collectors.toList());
    }

    private static MongoClient getMainMongoClient(String connectionString) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }
}
