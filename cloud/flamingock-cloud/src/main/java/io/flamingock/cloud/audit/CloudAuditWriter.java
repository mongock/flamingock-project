package io.flamingock.cloud.audit;

import io.flamingock.commons.utils.Result;
import io.flamingock.internal.core.engine.audit.AuditWriter;
import io.flamingock.internal.core.engine.audit.domain.ExecutionAuditItem;
import io.flamingock.internal.core.engine.audit.domain.RollbackAuditItem;
import io.flamingock.internal.core.engine.audit.domain.StartExecutionAuditItem;
import io.flamingock.internal.core.engine.audit.writer.AuditEntryMapper;

public interface CloudAuditWriter extends AuditWriter {

    default Result writeStartExecution(StartExecutionAuditItem auditItem) {
        return Result.OK();//TODO remove this
//        return writeEntry(AuditEntryMapper.map(auditItem));
    }


    default Result writeExecution(ExecutionAuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }

    default Result writeRollback(RollbackAuditItem auditItem) {
        return writeEntry(AuditEntryMapper.map(auditItem));
    }
}
