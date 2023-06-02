package io.flamingock.oss.driver.mongodb.sync.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.oss.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.oss.core.configuration.AbstractConfiguration;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4CollectionWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4DocumentWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.MongoSync4SessionManager;
import io.flamingock.oss.driver.common.mongodb.CollectionHelper;
import io.flamingock.oss.driver.common.mongodb.MongoDBMapper;
import io.flamingock.oss.driver.common.mongodb.SessionWrapper;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.oss.internal.driver.MongockAuditEntry;
import io.flamingock.oss.internal.driver.MongockAuditor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static io.flamingock.oss.internal.persistence.EntryField.KEY_AUTHOR;
import static io.flamingock.oss.internal.persistence.EntryField.KEY_CHANGE_ID;
import static io.flamingock.oss.internal.persistence.EntryField.KEY_EXECUTION_ID;

public class MongoSync4Auditor extends MongockAuditor {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfiguration.class);

    private final MongoCollection<Document> collection;
    private final MongoDBMapper<MongoSync4DocumentWrapper> mapper = new MongoDBMapper<>(() -> new MongoSync4DocumentWrapper(new Document()));
    private final MongoSync4SessionManager sessionManager;

    MongoSync4Auditor(MongoDatabase database,
                      String collectionName,
                      ReadWriteConfiguration readWriteConfiguration,
                      MongoSync4SessionManager sessionManager) {
        this.collection = database.getCollection(collectionName)
                .withReadConcern(readWriteConfiguration.getReadConcern())
                .withReadPreference(readWriteConfiguration.getReadPreference())
                .withWriteConcern(readWriteConfiguration.getWriteConcern());
        this.sessionManager = sessionManager;
    }

    @Override
    protected void initialize(boolean indexCreation) {
        new CollectionHelper<>(
                new MongoSync4CollectionWrapper(collection),
                () -> new MongoSync4DocumentWrapper(new Document()),
                new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID}
        ).initialize(indexCreation);
    }

    @Override
    protected Result writeEntry(MongockAuditEntry auditEntry) {
        Bson filter = Filters.and(
                Filters.eq(KEY_EXECUTION_ID, auditEntry.getExecutionId()),
                Filters.eq(KEY_CHANGE_ID, auditEntry.getChangeId()),
                Filters.eq(KEY_AUTHOR, auditEntry.getAuthor())
        );

        Document entryDocument = mapper.toDocument(auditEntry).getDocument();

        UpdateResult result = sessionManager.getSession(auditEntry.getChangeId())
                .map(SessionWrapper::getClientSession)
                .map(clientSession -> collection.replaceOne(clientSession, filter, entryDocument, new ReplaceOptions().upsert(true)))
                .orElseGet(() -> collection.replaceOne(filter, entryDocument, new ReplaceOptions().upsert(true)));
        logger.debug("SaveOrUpdate[{}] with result" +
                "\n[upsertId:{}, matches: {}, modifies: {}, acknowledged: {}]", auditEntry, result.getUpsertedId(), result.getMatchedCount(), result.getModifiedCount(), result.wasAcknowledged());

        return Result.OK();
    }


    @Override
    public SingleAuditProcessStatus getAuditProcessStatus() {
        SingleAuditProcessStatus.Builder builder = SingleAuditProcessStatus.builder();
        collection.find()
                .into(new LinkedList<>())
                .stream()
                .map(MongoSync4DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }
}
