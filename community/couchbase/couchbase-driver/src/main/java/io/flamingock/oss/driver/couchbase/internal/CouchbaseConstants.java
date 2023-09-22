package io.flamingock.oss.driver.couchbase.internal;

public final class CouchbaseConstants {
    public static final String INDEX_NAME = "idx_mongock_keys";
    public static final String DOCUMENT_TYPE_KEY = "_doctype";
    public static final String DOCUMENT_TYPE_AUDIT_ENTRY = "mongockChangeEntry"; //TODO: it should be flamingockAuditEntry
    public static final  String DOCUMENT_TYPE_LOCK_ENTRY = "mongockLockEntry"; //TODO: it should be flamingockLockEntry
}
