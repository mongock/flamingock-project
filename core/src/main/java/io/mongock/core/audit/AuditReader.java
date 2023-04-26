package io.mongock.core.audit;

import io.mongock.core.audit.domain.AuditProcessStatus;

public interface AuditReader<AUDIT_PROCESS_STATE extends AuditProcessStatus> {

    AUDIT_PROCESS_STATE getAuditProcessStatus();

}
