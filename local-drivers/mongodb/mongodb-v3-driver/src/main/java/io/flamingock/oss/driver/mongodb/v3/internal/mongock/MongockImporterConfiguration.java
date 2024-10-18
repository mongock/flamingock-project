package io.flamingock.oss.driver.mongodb.v3.internal.mongock;

import com.mongodb.client.MongoCollection;
import io.flamingock.core.api.annotations.NonLockGuarded;
import io.flamingock.core.engine.audit.AuditWriter;
import org.bson.Document;

@NonLockGuarded
public class MongockImporterConfiguration {

    private final MongoCollection<Document> sourceCollection;

    private final AuditWriter auditWriter;

    public MongockImporterConfiguration(MongoCollection<Document> sourceCollection, AuditWriter auditWriter) {
        this.sourceCollection = sourceCollection;
        this.auditWriter = auditWriter;
    }

    public MongoCollection<Document> getMongoDatabase() {
        return sourceCollection;
    }

    public AuditWriter getAuditWriter() {
        return auditWriter;
    }

    @Override
    public String toString() {
        return "MongockLegacyImporterConfiguration{" + "mongoDatabase=" + sourceCollection.toString() +
                ", auditWriter=" + auditWriter.toString() +
                '}';
    }
}
