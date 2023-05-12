package io.flamingock.internal.state;

import io.mongock.core.audit.domain.AuditEntry;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.util.Result;

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
