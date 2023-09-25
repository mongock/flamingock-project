package io.flamingock.oss.driver.couchbase.internal.entry;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonCreator;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.couchbase.client.java.json.JsonObject;

import io.flamingock.community.internal.persistence.MongockAuditEntry;
import io.flamingock.core.audit.domain.AuditEntryStatus;
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

/**
 * MongockAuditEntry implementation for Couchbase, basically adds a way to deserialize the object from JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouchbaseAuditEntry extends MongockAuditEntry {
  
  @JsonCreator
  public CouchbaseAuditEntry(JsonObject jsonObject){
    super(jsonObject.getString(KEY_EXECUTION_ID),
        jsonObject.getString(KEY_CHANGE_ID),
        jsonObject.getString(KEY_AUTHOR),
        jsonObject.get(KEY_TIMESTAMP) != null ? TimeUtil.toLocalDateTime(jsonObject.getLong(KEY_TIMESTAMP)) : null,
        jsonObject.get(KEY_STATE) != null ? AuditEntryStatus.valueOf(jsonObject.getString(KEY_STATE)) : null,
        jsonObject.get(KEY_TYPE) != null ? MongockAuditEntry.ExecutionType.valueOf(jsonObject.getString(KEY_TYPE)) : null,
        jsonObject.getString(KEY_CHANGELOG_CLASS),
        jsonObject.getString(KEY_CHANGESET_METHOD),
        jsonObject.getLong(KEY_EXECUTION_MILLIS),
        jsonObject.getString(KEY_EXECUTION_HOSTNAME),
        jsonObject.get(KEY_METADATA) != null ? jsonObject.getObject(KEY_METADATA).toMap() : null,
        jsonObject.getBoolean(KEY_SYSTEM_CHANGE),
        jsonObject.getString(KEY_ERROR_TRACE));
  }

}
