package io.mongock.internal.driver;

import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.task.descriptor.TaskDescriptor;

public abstract class MongockAuditor extends AuditWriter<MongockAuditEntry> implements AuditReader<SingleAuditProcessStatus> {


    @Override
    protected final MongockAuditEntry map(AuditItem auditItem) {
        TaskDescriptor taskDescriptor = auditItem.getTaskDescriptor();
        ExecutionContext executionContext = auditItem.getExecutionContext();
        RuntimeContext runtimeContext = auditItem.getRuntimeContext();
        return new MongockAuditEntry(
                executionContext.getExecutionId(),
                taskDescriptor.getId(),
                executionContext.getAuthor(),
                runtimeContext.getExecutedAt(),
                getAuditStatus(auditItem),
                taskDescriptor.getClassImplementor(),
                runtimeContext.getMethodExecutor(),
                runtimeContext.getDuration(),
                executionContext.getHostname(),
                executionContext.getMetadata(),
                runtimeContext.getError().orElse(null)
        );
    }


    private AuditEntryStatus getAuditStatus(AuditItem auditable) {
//        switch (auditable.getOperation()) {
//            case EXECUTION:
//                return auditable.getRuntimeContext()
//
//        }
        //TODO implement this
        return AuditEntryStatus.EXECUTED;
    }
}
