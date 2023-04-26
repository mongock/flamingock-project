package io.mongock.core.process.stubs;

import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.domain.AuditProcessStatus;

import java.util.Optional;

public class TestAuditProcessStatus implements AuditProcessStatus {
    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String entryId) {
        return Optional.empty();
    }
}
