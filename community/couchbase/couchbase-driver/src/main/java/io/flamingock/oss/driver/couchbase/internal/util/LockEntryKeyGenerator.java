package io.flamingock.oss.driver.couchbase.internal.util;

import io.flamingock.community.internal.persistence.LockEntry;
import io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants;

public class LockEntryKeyGenerator {
  
  public String toKey(LockEntry lockEntry) {
    return toKey(lockEntry.getKey());
  }
  
  public String toKey(String key) {
    return new StringBuilder()
        .append(CouchbaseConstants.DOCUMENT_TYPE_LOCK_ENTRY)
        .append('-')
        .append(key)
        .toString();
  }
}
