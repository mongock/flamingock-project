package io.flamingock.importer.cloud.mongodb.v4;

import com.mongodb.client.MongoCollection;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.importer.cloud.common.MongockLegacyAuditEntry;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDBLegacyImportConfiguration {

    private final MongoCollection<Document> changeUnitsStorage;
    private final EnvironmentId environmentId;
    private final String jwt;
    private final ServiceId serviceId;
    private final String serverHost;

    public MongoDBLegacyImportConfiguration(EnvironmentId environmentId,
                                            ServiceId serviceId,
                                            String jwt,
                                            String serverHost,
                                            MongoCollection<Document> changeUnitsStorage) {
        this.environmentId = environmentId;
        this.serviceId = serviceId;
        this.jwt = jwt;
        this.serverHost = serverHost;
        this.changeUnitsStorage = changeUnitsStorage;
    }

    static MongockLegacyAuditEntry toMongockLegacyAuditEntry(Document document) {
        return new MongockLegacyAuditEntry(
                document.getString("executionId"),
                document.getString("changeId"),
                document.getString("state"),
                document.getString("type"),
                document.getString("author"),
                document.getDate("timestamp"),
                document.getString("changeLogClass"),
                document.getString("changeSetMethod"),
                document.get("metadata"),
                document.getLong("executionMillis"),
                document.getString("executionHostName"),
                document.getString("errorTrace"),
                document.getBoolean("systemChange")
        );
    }

    public MongoCollection<Document> getChangeUnitsStorage() {
        return changeUnitsStorage;
    }

    public EnvironmentId getEnvironmentId() {
        return environmentId;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public String getJwt() {
        return jwt;
    }

    public String getServerHost() {
        return serverHost;
    }

    public List<MongockLegacyAuditEntry> readMongockLegacyAuditEntries() {
        return changeUnitsStorage
                .find()
                .into(new ArrayList<>())
                .stream()
                .map(MongoDBLegacyImportConfiguration::toMongockLegacyAuditEntry)
                .collect(Collectors.toList());
    }
}
