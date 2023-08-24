package io.flamingock.core.audit.domain;

public enum AuditEntryStatus {
    EXECUTED, FAILED, ROLLED_BACK, ROLLBACK_FAILED;

    public static boolean isRequiredExecution(AuditEntryStatus entryStatus) {
        return entryStatus == null || entryStatus == FAILED || entryStatus == ROLLED_BACK || entryStatus == ROLLBACK_FAILED;
    }


}
