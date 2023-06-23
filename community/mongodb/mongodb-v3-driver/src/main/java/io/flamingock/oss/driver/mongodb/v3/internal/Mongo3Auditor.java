package io.flamingock.oss.driver.mongodb.v3.internal;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.util.Result;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.common.mongodb.MongoDBAuditMapper;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.Mongo3CollectionWrapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.Mongo3DocumentWrapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.persistence.MongockAuditEntry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_AUTHOR;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_EXECUTION_ID;

public class Mongo3Auditor extends MongockAuditor {
    
    private static final Logger logger = LoggerFactory.getLogger(Mongo3Auditor.class);


    private final MongoCollection<Document> collection;
    private final MongoDBAuditMapper<Mongo3DocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new Mongo3DocumentWrapper(new Document()));
    private final SessionManager<ClientSession> sessionManager;

    Mongo3Auditor(MongoDatabase database,
                      String collectionName,
                      ReadWriteConfiguration readWriteConfiguration,
                      SessionManager<ClientSession> sessionManager) {
        this.collection = database.getCollection(collectionName)
                .withReadConcern(readWriteConfiguration.getReadConcern())
                .withReadPreference(readWriteConfiguration.getReadPreference())
                .withWriteConcern(readWriteConfiguration.getWriteConcern());
        this.sessionManager = sessionManager;
    }

    @Override
    protected void initialize(boolean indexCreation) {
        CollectionInitializator<Mongo3DocumentWrapper> initializer = new CollectionInitializator<>(
                new Mongo3CollectionWrapper(collection),
                () -> new Mongo3DocumentWrapper(new Document()),
                new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID}
        );
        if(indexCreation) {
            initializer.initialize();
        } else {
            initializer.justValidateCollection();
        }

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
                .map(Mongo3DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }
}