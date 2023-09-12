package io.flamingock.oss.driver.mongodb.springdata.v4.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.util.Result;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.common.mongodb.MongoDBAuditMapper;
import io.flamingock.oss.driver.mongodb.springdata.v4.internal.mongodb.SpringDataMongoV4CollectionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v4.internal.mongodb.SpringDataMongoV4DocumentWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.community.internal.driver.MongockAuditor;
import io.flamingock.community.internal.persistence.MongockAuditEntry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_AUTHOR;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_EXECUTION_ID;

public class SpringDataMongoV4Auditor extends MongockAuditor {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV4Auditor.class);

    private final MongoCollection<Document> collection;
    private final MongoDBAuditMapper<SpringDataMongoV4DocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new SpringDataMongoV4DocumentWrapper(new Document()));

    SpringDataMongoV4Auditor(MongoTemplate mongoTemplate,
                      String collectionName,
                      ReadWriteConfiguration readWriteConfiguration) {
        this.collection = mongoTemplate.getCollection(collectionName)
                .withReadConcern(readWriteConfiguration.getReadConcern())
                .withReadPreference(readWriteConfiguration.getReadPreference())
                .withWriteConcern(readWriteConfiguration.getWriteConcern());
    }

    @Override
    protected void initialize(boolean indexCreation) {
        CollectionInitializator<SpringDataMongoV4DocumentWrapper> initializer = new CollectionInitializator<>(
                new SpringDataMongoV4CollectionWrapper(collection),
                () -> new SpringDataMongoV4DocumentWrapper(new Document()),
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

        UpdateResult result = collection.replaceOne(filter, entryDocument, new ReplaceOptions().upsert(true));
        logger.debug("SaveOrUpdate[{}] with result" +
                "\n[upsertId:{}, matches: {}, modifies: {}, acknowledged: {}]", auditEntry, result.getUpsertedId(), result.getMatchedCount(), result.getModifiedCount(), result.wasAcknowledged());

        return Result.OK();
    }


    @Override
    public SingleAuditStageStatus getAuditStageStatus() {
        SingleAuditStageStatus.Builder builder = SingleAuditStageStatus.builder();
        collection.find()
                .into(new LinkedList<>())
                .stream()
                .map(SpringDataMongoV4DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }
}