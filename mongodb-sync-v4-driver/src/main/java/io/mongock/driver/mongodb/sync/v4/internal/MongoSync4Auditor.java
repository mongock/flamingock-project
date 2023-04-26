package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.internal.driver.MongockAuditEntry;
import io.mongock.internal.driver.MongockAuditor;
import org.bson.Document;

public class MongoSync4Auditor extends MongockAuditor {

    private final MongoCollection<Document> collection;

    MongoSync4Auditor(MongoDatabase database, String collectionName) {
        this.collection = database.getCollection(collectionName);
    }

    @Override
    protected AuditResult writeEntry(MongockAuditEntry auditEntry) {
        //TODO implement
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public SingleAuditProcessStatus getAuditProcessStatus() {
        //TODO implement
        throw new RuntimeException("NOT IMPLEMENTED");
    }
}
