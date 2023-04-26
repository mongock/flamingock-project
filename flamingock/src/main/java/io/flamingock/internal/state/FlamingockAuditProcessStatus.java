package io.flamingock.internal.state;

import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.domain.AuditProcessStatus;

import java.util.Optional;

public class FlamingockAuditProcessStatus implements AuditProcessStatus {
    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String entryId) {
        return Optional.empty();
    }
}
