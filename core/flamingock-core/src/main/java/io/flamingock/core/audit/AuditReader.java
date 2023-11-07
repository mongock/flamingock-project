package io.flamingock.core.audit;

import io.flamingock.core.audit.writer.AuditStageStatus;

public interface AuditReader {
    AuditStageStatus getAuditStageStatus();
}
