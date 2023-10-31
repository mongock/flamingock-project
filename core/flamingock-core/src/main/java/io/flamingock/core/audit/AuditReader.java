package io.flamingock.core.audit;

import io.flamingock.core.audit.domain.AuditStageStatus;

public interface AuditReader {
    AuditStageStatus getAuditStageStatus();
}
