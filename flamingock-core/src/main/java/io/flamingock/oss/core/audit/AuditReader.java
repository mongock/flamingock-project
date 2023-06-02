package io.flamingock.oss.core.audit;

import io.flamingock.oss.core.audit.domain.AuditProcessStatus;

public interface AuditReader<AUDIT_PROCESS_STATE extends AuditProcessStatus> {

    AUDIT_PROCESS_STATE getAuditProcessStatus();

}
