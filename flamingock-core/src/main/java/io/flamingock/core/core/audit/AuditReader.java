package io.flamingock.core.core.audit;

import io.flamingock.core.core.audit.domain.AuditProcessStatus;

public interface AuditReader<AUDIT_PROCESS_STATE extends AuditProcessStatus> {

    AUDIT_PROCESS_STATE getAuditProcessStatus();

}
