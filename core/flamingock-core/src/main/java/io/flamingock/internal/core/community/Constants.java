package io.flamingock.internal.core.community;

public final class Constants {

    public static final String DEFAULT_AUDIT_STORE_NAME = "flamingockAuditLogs";
    public static final String AUDIT_LOG_PK = "partitionKey";
    public static final String AUDIT_LOG_STAGE_ID = "stageId";

    public static final String DEFAULT_LOCK_STORE_NAME = "flamingockLocks";
    public static final String LOCK_PK = "partitionKey";
    public static final String LOCK_OWNER = "lockOwner";

    private Constants() {
    }
}
