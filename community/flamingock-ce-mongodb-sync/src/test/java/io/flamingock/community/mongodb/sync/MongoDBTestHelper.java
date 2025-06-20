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

package io.flamingock.community.mongodb.sync;

import com.mongodb.client.MongoDatabase;
import io.flamingock.internal.common.core.audit.AuditEntry;
import io.flamingock.internal.util.TimeUtil;
import io.flamingock.internal.common.mongodb.MongoDBAuditMapper;
import io.flamingock.cloud.transaction.mongodb.sync.wrapper.MongoSync4DocumentWrapper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.flamingock.internal.core.community.Constants.KEY_CHANGE_ID;
import static io.flamingock.internal.core.community.Constants.KEY_TIMESTAMP;

public class MongoDBTestHelper {
    public final MongoDatabase mongoDatabase;
    private final MongoDBAuditMapper<MongoSync4DocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new MongoSync4DocumentWrapper(new Document()));

    public MongoDBTestHelper(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public boolean collectionExists(String collectionName) {
        return mongoDatabase.listCollectionNames().into(new ArrayList()).contains(collectionName);
    }

    public List<String> getAuditLogSorted(String auditLogCollection) {
        return mongoDatabase.getCollection(auditLogCollection)
                .find()
                .into(new LinkedList<>())
                .stream()
                .sorted(Comparator.comparing(d -> TimeUtil.toLocalDateTime(d.get(KEY_TIMESTAMP))))
                .map(document -> document.getString(KEY_CHANGE_ID))
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getAuditEntriesSorted(String auditLogCollection) {
        return mongoDatabase.getCollection(auditLogCollection).find()
                .into(new LinkedList<>())
                .stream()
                .map(MongoSync4DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList());
    }

}
