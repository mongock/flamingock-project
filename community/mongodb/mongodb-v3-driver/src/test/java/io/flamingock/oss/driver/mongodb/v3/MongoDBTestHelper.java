package io.flamingock.oss.driver.mongodb.v3;

import com.mongodb.client.MongoDatabase;

import io.flamingock.community.internal.persistence.MongockAuditEntry;
import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.oss.driver.common.mongodb.MongoDBAuditMapper;
import io.flamingock.oss.driver.mongodb.v3.internal.mongodb.Mongo3DocumentWrapper;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_TIMESTAMP;

public class MongoDBTestHelper {
    public final MongoDatabase mongoDatabase;
    private final MongoDBAuditMapper<Mongo3DocumentWrapper> mapper = new MongoDBAuditMapper<>(() -> new Mongo3DocumentWrapper(new Document()));

    public MongoDBTestHelper(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
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

    public List<MongockAuditEntry> getAuditEntriesSorted(String auditLogCollection) {
        return mongoDatabase.getCollection(auditLogCollection).find()
                .into(new LinkedList<>())
                .stream()
                .map(Mongo3DocumentWrapper::new)
                .map(mapper::fromDocument)
                .collect(Collectors.toList());
    }
}
