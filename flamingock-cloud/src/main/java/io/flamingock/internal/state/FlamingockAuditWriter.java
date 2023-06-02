package io.flamingock.internal.state;

import io.flamingock.oss.core.audit.domain.AuditEntry;
import io.flamingock.oss.core.audit.writer.AuditItem;
import io.flamingock.oss.core.audit.writer.AuditWriter;
import io.flamingock.oss.core.util.Result;

public class FlamingockAuditWriter extends AuditWriter<AuditEntry> {


    @Override
    protected Result writeEntry(AuditEntry auditEntry) {
        return null;
    }

    @Override
    protected AuditEntry map(AuditItem taskStep) {
        return null;
    }

}
