package io.mongock.internal.driver;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.domain.AuditEntryStatus;

import java.time.LocalDateTime;

public class MongockAuditEntry extends AuditEntry {
    public MongockAuditEntry(String executionId,
                                String changeId,
                                String author,
                                LocalDateTime timestamp,
                                AuditEntryStatus state,
                                String changeLogClass,
                                String changeSetMethod,
                                long executionMillis,
                                String executionHostname,
                                Object metadata,
                                Throwable errorTrace) {
        super(executionId, changeId, author, timestamp, state, changeLogClass, changeSetMethod, executionMillis, executionHostname, metadata, errorTrace);
    }
}
