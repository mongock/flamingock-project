package io.flamingock.oss.driver.mongodb.v3.internal.legacy;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.core.engine.audit.AuditWriter;

@NonLockGuarded
public class MongockLegacyImporterConfiguration {

    private final MongoDatabase mongoDatabase;

    private final AuditWriter auditWriter;

    public MongockLegacyImporterConfiguration(MongoDatabase mongoDatabase, AuditWriter auditWriter) {
        this.mongoDatabase = mongoDatabase;
        this.auditWriter = auditWriter;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public AuditWriter getAuditWriter() {
        return auditWriter;
    }

    @Override
    public String toString() {
        return "MongockLegacyImporterConfiguration{" + "mongoDatabase=" + mongoDatabase.toString() +
                ", auditWriter=" + auditWriter.toString() +
                '}';
    }
}
