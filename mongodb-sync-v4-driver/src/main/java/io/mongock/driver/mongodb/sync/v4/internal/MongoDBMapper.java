package io.mongock.driver.mongodb.sync.v4.internal;

import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.util.DateUtil;
import io.mongock.internal.driver.MongockAuditEntry;
import org.bson.Document;

import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_AUTHOR;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_CHANGELOG_CLASS;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_CHANGESET_METHOD;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_CHANGE_ID;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_ERROR_TRACE;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_EXECUTION_HOSTNAME;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_EXECUTION_ID;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_EXECUTION_MILLIS;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_METADATA;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_STATE;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_SYSTEM_CHANGE;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_TIMESTAMP;
import static io.mongock.driver.mongodb.sync.v4.internal.EntryField.KEY_TYPE;

public final class MongoDBMapper {

    private MongoDBMapper() {
    }

    /**
     * ("executionId"),
     * ("changeId"),
     * ("author"),
     * ("timestamp"),
     * ("state"),
     * ("type"),
     * ("changeLogClass"),
     * ("changeSetMethod"),
     * ("metadata"),
     * ("executionMillis"),
     * ("executionHostname"),
     * ("errorTrace"),
     * ("systemChange");
     */
    public static Document toDocument(MongockAuditEntry auditEntry) {
        Document document = new Document();
        document.append(KEY_EXECUTION_ID, auditEntry.getExecutionId());
        document.append(KEY_CHANGE_ID, auditEntry.getChangeId());
        document.append(KEY_AUTHOR, auditEntry.getAuthor());
        document.append(KEY_TIMESTAMP, DateUtil.toDate(auditEntry.getTimestamp()));
        document.append(KEY_STATE, auditEntry.getState().name());
        document.append(KEY_TYPE, auditEntry.getType());
        document.append(KEY_CHANGELOG_CLASS, auditEntry.getChangeLogClass());
        document.append(KEY_CHANGESET_METHOD, auditEntry.getChangeSetMethod());
        document.append(KEY_METADATA, auditEntry.getMetadata());
        document.append(KEY_EXECUTION_MILLIS, auditEntry.getExecutionMillis());
        document.append(KEY_EXECUTION_HOSTNAME, auditEntry.getExecutionHostname());
        document.append(KEY_ERROR_TRACE, auditEntry.getErrorTrace());
        document.append(KEY_SYSTEM_CHANGE, auditEntry.getSystemChange());
        return document;
    }

    public static MongockAuditEntry fromDocument(Document entry) {
        return new MongockAuditEntry(
                entry.getString(KEY_EXECUTION_ID),
                entry.getString(KEY_CHANGE_ID),
                entry.getString(KEY_AUTHOR),
                DateUtil.toLocalDateTime(entry.get(KEY_TIMESTAMP)),
                entry.containsKey(KEY_STATE) ? AuditEntryStatus.valueOf(entry.getString(KEY_STATE)) : null,
                entry.containsKey(KEY_TYPE) ? MongockAuditEntry.ExecutionType.valueOf(entry.getString(KEY_TYPE)) : null,
                entry.getString(KEY_CHANGELOG_CLASS),
                entry.getString(KEY_CHANGESET_METHOD),
                entry.containsKey(KEY_EXECUTION_MILLIS) && entry.get(KEY_EXECUTION_MILLIS) != null
                        ? ((Number) entry.get(KEY_EXECUTION_MILLIS)).longValue() : -1L,
                entry.getString(KEY_EXECUTION_HOSTNAME),
                entry.get(KEY_METADATA),
                entry.getBoolean(KEY_SYSTEM_CHANGE) != null && entry.getBoolean(KEY_SYSTEM_CHANGE),
                entry.getString(KEY_ERROR_TRACE));

    }

}
