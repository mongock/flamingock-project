package io.mongock.driver.mongodb.sync.v4.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.mongodb.MongoDBMapper;
import io.mongock.driver.mongodb.sync.v4.internal.mongodb.DocumentckImpl;
import io.mongock.internal.driver.MongockAuditEntry;
import io.mongock.internal.driver.MongockAuditor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.mongock.internal.persistence.EntryField.KEY_AUTHOR;
import static io.mongock.internal.persistence.EntryField.KEY_CHANGE_ID;
import static io.mongock.internal.persistence.EntryField.KEY_EXECUTION_ID;

public class MongoSync4Auditor extends MongockAuditor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfiguration.class);

    private final MongoCollection<Document> collection;
    private final MongoDBMapper<DocumentckImpl> mapper = new MongoDBMapper<>(() -> new DocumentckImpl(new Document()));

    MongoSync4Auditor(MongoDatabase database, String collectionName) {
        this.collection = database.getCollection(collectionName);
    }

    @Override
    protected AuditResult writeEntry(MongockAuditEntry auditEntry) {
        Bson filter = Filters.and(
                Filters.eq(KEY_EXECUTION_ID, auditEntry.getExecutionId()),
                Filters.eq(KEY_CHANGE_ID, auditEntry.getChangeId()),
                Filters.eq(KEY_AUTHOR, auditEntry.getAuthor())
        );

        Document entryDocument = mapper.toDocument(auditEntry).getDocument();

        UpdateResult result = getClientSession()
                .map(clientSession -> collection.replaceOne(clientSession, filter, entryDocument, new ReplaceOptions().upsert(true)))
                .orElseGet(() -> collection.replaceOne(filter, entryDocument, new ReplaceOptions().upsert(true)));
        logger.debug("SaveOrUpdate[{}] with result" +
                "\n[upsertId:{}, matches: {}, modifies: {}, acknowledged: {}]", auditEntry, result.getUpsertedId(), result.getMatchedCount(), result.getModifiedCount(), result.wasAcknowledged());

        return AuditResult.OK();
    }


    @Override
    public SingleAuditProcessStatus getAuditProcessStatus() {
        SingleAuditProcessStatus.Builder builder = SingleAuditProcessStatus.builder();
        collection.find()
                .into(new LinkedList<>())
                .stream()
                .map(DocumentckImpl::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }


    //TODO implement
    private Optional<ClientSession> getClientSession() {
        return Optional.empty();
    }
}
