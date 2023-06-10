package io.flamingock.cloud.state;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;

import java.util.Optional;

public class FlamingockAuditProcessStatus implements AuditProcessStatus {
    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String entryId) {
        return Optional.empty();
    }
}