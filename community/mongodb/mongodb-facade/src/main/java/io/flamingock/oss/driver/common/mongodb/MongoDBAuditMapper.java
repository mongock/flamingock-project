package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.community.internal.persistence.MongockAuditEntry;

import java.util.function.Supplier;

import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_AUTHOR;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGELOG_CLASS;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGESET_METHOD;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_CHANGE_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_ERROR_TRACE;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_EXECUTION_HOSTNAME;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_EXECUTION_ID;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_EXECUTION_MILLIS;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_METADATA;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_STATE;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_SYSTEM_CHANGE;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_TIMESTAMP;
import static io.flamingock.community.internal.persistence.AuditEntryField.KEY_TYPE;

public class MongoDBAuditMapper<DOCUMENT_WRAPPER extends DocumentWrapper> {

    private final Supplier<DOCUMENT_WRAPPER> documentckSupplier;

    public MongoDBAuditMapper(Supplier<DOCUMENT_WRAPPER> documentCreator) {
        this.documentckSupplier = documentCreator;
    }

    public DOCUMENT_WRAPPER toDocument(MongockAuditEntry auditEntry) {
        DOCUMENT_WRAPPER document = documentckSupplier.get();
        document.append(KEY_EXECUTION_ID, auditEntry.getExecutionId());
        document.append(KEY_CHANGE_ID, auditEntry.getChangeId());
        document.append(KEY_AUTHOR, auditEntry.getAuthor());
        document.append(KEY_TIMESTAMP, TimeUtil.toDate(auditEntry.getTimestamp()));
        document.append(KEY_STATE, auditEntry.getState().name());
        document.append(KEY_TYPE, auditEntry.getType().name());
        document.append(KEY_CHANGELOG_CLASS, auditEntry.getChangeLogClass());
        document.append(KEY_CHANGESET_METHOD, auditEntry.getChangeSetMethod());
        document.append(KEY_METADATA, auditEntry.getMetadata());
        document.append(KEY_EXECUTION_MILLIS, auditEntry.getExecutionMillis());
        document.append(KEY_EXECUTION_HOSTNAME, auditEntry.getExecutionHostname());
        document.append(KEY_ERROR_TRACE, auditEntry.getErrorTrace());
        document.append(KEY_SYSTEM_CHANGE, auditEntry.getSystemChange());
        return document;
    }

    public MongockAuditEntry fromDocument(DocumentWrapper entry) {
        return new MongockAuditEntry(
                entry.getString(KEY_EXECUTION_ID),
                entry.getString(KEY_CHANGE_ID),
                entry.getString(KEY_AUTHOR),
                TimeUtil.toLocalDateTime(entry.get(KEY_TIMESTAMP)),
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
