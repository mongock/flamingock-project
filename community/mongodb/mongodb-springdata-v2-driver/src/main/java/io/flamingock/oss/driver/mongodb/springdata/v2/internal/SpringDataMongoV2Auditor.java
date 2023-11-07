package io.flamingock.oss.driver.mongodb.springdata.v2.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.core.audit.writer.AuditStageStatus;
import io.flamingock.core.util.Result;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.common.mongodb.MongoDBAuditMapper;
import io.flamingock.oss.driver.mongodb.springdata.v2.internal.mongodb.SpringDataMongoV2CollectionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.v2.internal.mongodb.SpringDataMongoV2DocumentWrapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.ReadWriteConfiguration;
import io.flamingock.core.audit.Auditor;
import io.flamingock.core.audit.writer.AuditEntry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static io.flamingock.community.internal.AuditEntryField.KEY_AUTHOR;
import static io.flamingock.community.internal.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.AuditEntryField.KEY_EXECUTION_ID;

public class SpringDataMongoV2Auditor implements Auditor {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2Auditor.class);

    private final MongoCollection<Document> collection;
    private final MongoDBAuditMapper<SpringDataMongoV2DocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new SpringDataMongoV2DocumentWrapper(new Document()));

    SpringDataMongoV2Auditor(MongoTemplate mongoTemplate,
                      String collectionName,
                      ReadWriteConfiguration readWriteConfiguration) {
        this.collection = mongoTemplate.getCollection(collectionName)
                .withReadConcern(readWriteConfiguration.getReadConcern())
                .withReadPreference(readWriteConfiguration.getReadPreference())
                .withWriteConcern(readWriteConfiguration.getWriteConcern());
    }

    protected void initialize(boolean indexCreation) {
        CollectionInitializator<SpringDataMongoV2DocumentWrapper> initializer = new CollectionInitializator<>(
                new SpringDataMongoV2CollectionWrapper(collection),
                () -> new SpringDataMongoV2DocumentWrapper(new Document()),
                new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID}
        );
        if(indexCreation) {
            initializer.initialize();
        } else {
            initializer.justValidateCollection();
        }

    }

    @Override
    public Result writeEntry(AuditEntry auditEntry) {

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
    public AuditStageStatus getAuditStageStatus() {
        AuditStageStatus.Builder builder = AuditStageStatus.builder();
        collection.find()
                .into(new LinkedList<>())
                .stream()
                .map(SpringDataMongoV2DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }
}
