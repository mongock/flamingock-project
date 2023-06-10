package io.flamingock.cloud.state;

import io.flamingock.core.core.audit.domain.AuditEntry;
import io.flamingock.core.core.audit.writer.AbstractAuditWriter;
import io.flamingock.core.core.audit.writer.AuditItem;
import io.flamingock.core.core.util.Result;

public class FlamingockAuditWriter extends AbstractAuditWriter<AuditEntry> {


    @Override
    protected Result writeEntry(AuditEntry auditEntry) {
        return null;
    }

    @Override
    protected AuditEntry map(AuditItem taskStep) {
        return null;
    }

}
