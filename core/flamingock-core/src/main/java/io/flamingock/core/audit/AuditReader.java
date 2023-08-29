package io.flamingock.core.audit;

import io.flamingock.core.audit.domain.AuditStageStatus;

public interface AuditReader<AUDIT_STAGE_STATUS extends AuditStageStatus> {

    AUDIT_STAGE_STATUS getAuditStageStatus();

}
