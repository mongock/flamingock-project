package io.mongock.core.audit.writer;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.domain.AuditResult;

/**
 * This class implements the Facade pattern containing the responsibility to log the taskStep, map it to Entry
 * and log then entry just in order to avoid having too many classes to implement that depend on the
 * same AuditEntry implementation, which would enforce to add the generic to the holder/orchestrator class.
 * <br />
 * However, the `mapper responsibility` is intended to be delegated to a Mapper class, but that's is left to decide
 * to the developer implementing this abstract class.
 */
public abstract class AuditWriter<AUDIT_ENTRY extends AuditEntry> {

    public final AuditResult writeStep(AuditItem auditItem) {
        return writeEntry(map(auditItem));
    }

    protected abstract AuditResult writeEntry(AUDIT_ENTRY auditEntry);

    protected abstract AUDIT_ENTRY map(AuditItem taskStep);

}
