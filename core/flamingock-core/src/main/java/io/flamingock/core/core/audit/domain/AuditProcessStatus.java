package io.flamingock.core.core.audit.domain;

import java.util.Optional;

public interface AuditProcessStatus {
    Optional<AuditEntryStatus> getEntryStatus(String entryId);

}