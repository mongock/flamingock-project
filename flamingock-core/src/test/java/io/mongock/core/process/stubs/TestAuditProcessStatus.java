package io.mongock.core.process.stubs;

import io.flamingock.oss.core.audit.domain.AuditEntryStatus;
import io.flamingock.oss.core.audit.domain.AuditProcessStatus;

import java.util.Optional;

public class TestAuditProcessStatus implements AuditProcessStatus {
    @Override
    public Optional<AuditEntryStatus> getEntryStatus(String entryId) {
        return Optional.empty();
    }
}