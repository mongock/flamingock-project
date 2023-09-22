package io.flamingock.oss.driver.couchbase.internal.entry;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonCreator;
import com.couchbase.client.java.json.JsonObject;

import io.flamingock.community.internal.persistence.LockEntry;
import io.flamingock.core.lock.LockStatus;
import io.flamingock.core.util.TimeUtil;
import io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants;

import static io.flamingock.community.internal.persistence.LockEntryField.KEY_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.OWNER_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.STATUS_FIELD;
import static io.flamingock.community.internal.persistence.LockEntryField.EXPIRES_AT_FIELD;;

/**
 * LockEntry implementation for Couchbase, basically adds a way to deserialize the object from JSON.
 */
public class CouchbaseLockEntry extends LockEntry {

  private final String docType;

  @JsonCreator
  public CouchbaseLockEntry(JsonObject jsonObject) {
    super(jsonObject.getString(KEY_FIELD),
            jsonObject.containsKey(STATUS_FIELD) ? LockStatus.valueOf(jsonObject.getString(STATUS_FIELD)) : null,
            jsonObject.getString(OWNER_FIELD),
            TimeUtil.toLocalDateTime(jsonObject.getLong(EXPIRES_AT_FIELD)));
    this.docType = jsonObject.getString(CouchbaseConstants.DOCUMENT_TYPE_KEY);
  }

  public String getDocType() {
    return docType;
  }
}
