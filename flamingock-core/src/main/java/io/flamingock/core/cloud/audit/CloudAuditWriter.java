package io.flamingock.core.cloud.audit;

import io.flamingock.commons.utils.Result;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.domain.StartExecutionAuditItem;
import io.flamingock.core.engine.audit.writer.AuditEntryMapper;

public interface CloudAuditWriter extends AuditWriter {

    default Result writeStep(StartExecutionAuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }
}
