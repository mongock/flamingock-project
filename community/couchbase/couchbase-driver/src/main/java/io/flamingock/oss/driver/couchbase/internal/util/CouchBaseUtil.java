package io.flamingock.oss.driver.couchbase.internal.util;

import com.couchbase.client.java.json.JsonObject;
import io.flamingock.community.internal.persistence.LockEntry;
import io.flamingock.core.audit.writer.AuditEntry;
import io.flamingock.core.audit.writer.AuditEntryStatus;
import io.flamingock.core.lock.LockStatus;
import io.flamingock.core.util.TimeUtil;

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
import static io.flamingock.community.internal.persistence.LockEntryField.EXPIRES_AT_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.KEY_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.OWNER_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.STATUS_FIELD;

public final class CouchBaseUtil {
    private CouchBaseUtil() {
    }

    public static AuditEntry auditEntryFromEntity(JsonObject jsonObject) {
        return new AuditEntry(jsonObject.getString(KEY_EXECUTION_ID),
                jsonObject.getString(KEY_CHANGE_ID),
                jsonObject.getString(KEY_AUTHOR),
                jsonObject.get(KEY_TIMESTAMP) != null ? TimeUtil.toLocalDateTime(jsonObject.getLong(KEY_TIMESTAMP)) : null,
                jsonObject.get(KEY_STATE) != null ? AuditEntryStatus.valueOf(jsonObject.getString(KEY_STATE)) : null,
                jsonObject.get(KEY_TYPE) != null ? AuditEntry.ExecutionType.valueOf(jsonObject.getString(KEY_TYPE)) : null,
                jsonObject.getString(KEY_CHANGELOG_CLASS),
                jsonObject.getString(KEY_CHANGESET_METHOD),
                jsonObject.getLong(KEY_EXECUTION_MILLIS),
                jsonObject.getString(KEY_EXECUTION_HOSTNAME),
                jsonObject.get(KEY_METADATA) != null ? jsonObject.getObject(KEY_METADATA).toMap() : null,
                jsonObject.getBoolean(KEY_SYSTEM_CHANGE),
                jsonObject.getString(KEY_ERROR_TRACE));
    }

    public static LockEntry lockEntryFromEntity(JsonObject jsonObject) {
        return new LockEntry(jsonObject.getString(KEY_FIELD),
                jsonObject.containsKey(STATUS_FIELD) ? LockStatus.valueOf(jsonObject.getString(STATUS_FIELD)) : null,
                jsonObject.getString(OWNER_FIELD),
                TimeUtil.toLocalDateTime(jsonObject.getLong(EXPIRES_AT_FIELD)));
    }
}
