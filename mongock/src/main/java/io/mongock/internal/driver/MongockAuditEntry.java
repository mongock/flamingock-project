package io.mongock.internal.driver;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.domain.AuditEntryStatus;

import java.time.LocalDateTime;
import java.util.Date;

public class MongockAuditEntry extends AuditEntry {

    public enum ExecutionType {EXECUTION, BEFORE_EXECUTION}

    private final ExecutionType type;

    protected Boolean systemChange;


    public MongockAuditEntry(String executionId,
                             String changeId,
                             String author,
                             LocalDateTime timestamp,
                             AuditEntryStatus state,
                             ExecutionType type,
                             String className,
                             String methodName,
                             long executionMillis,
                             String executionHostname,
                             Object metadata,
                             boolean systemChange,
                             String errorTrace) {
        super(
                executionId,
                changeId,
                author,
                timestamp,
                state,
                className,
                methodName,
                executionMillis,
                executionHostname,
                metadata,
                errorTrace);
        this.type = type;

        this.systemChange = systemChange;
    }

    public LocalDateTime getTimestamp() {
        return getCreatedAt();
    }

    public String getChangeLogClass() {
        return getClassName();
    }

    public String getChangeSetMethod() {
        return getMethodName();
    }

    public Boolean getSystemChange() {
        return systemChange;
    }

    public ExecutionType getType() {
        return type;
    }
}
