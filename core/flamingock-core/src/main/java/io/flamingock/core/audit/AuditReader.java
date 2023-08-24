package io.flamingock.core.audit;

import io.flamingock.core.audit.domain.AuditStageStatus;

public interface AuditReader<AUDIT_PROCESS_STATE extends AuditStageStatus> {

    AUDIT_PROCESS_STATE getAuditProcessStatus();

}
