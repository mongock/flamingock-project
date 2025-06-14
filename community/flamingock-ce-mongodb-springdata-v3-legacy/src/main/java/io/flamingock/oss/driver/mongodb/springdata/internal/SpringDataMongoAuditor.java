/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.oss.driver.mongodb.springdata.internal;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.flamingock.commons.utils.Result;
import io.flamingock.internal.core.community.LocalAuditor;
import io.flamingock.internal.core.engine.audit.domain.AuditEntry;
import io.flamingock.internal.core.engine.audit.domain.AuditStageStatus;
import io.flamingock.oss.driver.common.mongodb.CollectionInitializator;
import io.flamingock.oss.driver.common.mongodb.MongoDBAuditMapper;
import io.flamingock.oss.driver.mongodb.springdata.internal.mongodb.SpringDataMongoCollectionWrapper;
import io.flamingock.oss.driver.mongodb.springdata.internal.mongodb.SpringDataMongoDocumentWrapper;
import io.flamingock.oss.driver.mongodb.sync.v4.internal.ReadWriteConfiguration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static io.flamingock.internal.core.community.AuditEntryField.KEY_AUTHOR;
import static io.flamingock.internal.core.community.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.internal.core.community.AuditEntryField.KEY_EXECUTION_ID;
import static io.flamingock.internal.core.community.AuditEntryField.KEY_STATE;

public class SpringDataMongoAuditor implements LocalAuditor {

    private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoAuditor.class);

    private final MongoCollection<Document> collection;
    private final MongoDBAuditMapper<SpringDataMongoDocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new SpringDataMongoDocumentWrapper(new Document()));

    SpringDataMongoAuditor(MongoTemplate mongoTemplate,
                           String collectionName,
                           ReadWriteConfiguration readWriteConfiguration) {
        this.collection = mongoTemplate.getCollection(collectionName)
                .withReadConcern(readWriteConfiguration.getReadConcern())
                .withReadPreference(readWriteConfiguration.getReadPreference())
                .withWriteConcern(readWriteConfiguration.getWriteConcern());
    }

    protected void initialize(boolean indexCreation) {
        CollectionInitializator<SpringDataMongoDocumentWrapper> initializer = new CollectionInitializator<>(
                new SpringDataMongoCollectionWrapper(collection),
                () -> new SpringDataMongoDocumentWrapper(new Document()),
                new String[]{KEY_EXECUTION_ID, KEY_CHANGE_ID, KEY_STATE}
        );
        if (indexCreation) {
            initializer.initialize();
        } else {
            initializer.justValidateCollection();
        }

    }

    @Override
    public Result writeEntry(AuditEntry auditEntry) {

        Bson filter = Filters.and(
                Filters.eq(KEY_EXECUTION_ID, auditEntry.getExecutionId()),
                Filters.eq(KEY_CHANGE_ID, auditEntry.getTaskId()),
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
        AuditStageStatus.EntryBuilder builder = AuditStageStatus.entryBuilder();
        collection.find()
                .into(new LinkedList<>())
                .stream()
                .map(SpringDataMongoDocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList())
                .forEach(builder::addEntry);
        return builder.build();

    }
}
