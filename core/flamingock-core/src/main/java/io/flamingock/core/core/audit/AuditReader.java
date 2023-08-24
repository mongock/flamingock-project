package io.flamingock.core.core.audit;

import io.flamingock.core.core.audit.domain.AuditStageStatus;

public interface AuditReader<AUDIT_PROCESS_STATE extends AuditStageStatus> {

    AUDIT_PROCESS_STATE getAuditProcessStatus();

}
