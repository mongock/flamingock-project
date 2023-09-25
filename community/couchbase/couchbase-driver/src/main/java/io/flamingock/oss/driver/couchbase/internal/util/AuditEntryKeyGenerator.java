package io.flamingock.oss.driver.couchbase.internal.util;

import io.flamingock.core.audit.domain.AuditEntry;
import io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants;

public class AuditEntryKeyGenerator {

  public String toKey(AuditEntry auditEntry) {
    return new StringBuilder()
        .append(CouchbaseConstants.DOCUMENT_TYPE_AUDIT_ENTRY)
        .append('-')
        .append(auditEntry.getExecutionId())
        .append('-')
        .append(auditEntry.getAuthor())
        .append('-')
        .append(auditEntry.getChangeId()).toString();
  }
}
