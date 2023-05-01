package io.mongock.internal.driver;

import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.util.ThrowableUtil;

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
                getExecutionType(auditItem),
                taskDescriptor.getClassImplementor(),
                runtimeContext.getMethodExecutor(),
                runtimeContext.getDuration(),
                executionContext.getHostname(),
                executionContext.getMetadata(),
                getSystemChange(auditItem),
                ThrowableUtil.serialize(runtimeContext.getError().orElse(null))
        );
    }

    protected abstract void initialize(boolean indexCreation);

    //TODO implement
    private boolean getSystemChange(AuditItem auditItem) {
        return false;
    }

    //TODO implement
    private MongockAuditEntry.ExecutionType getExecutionType(AuditItem auditItem) {
        return MongockAuditEntry.ExecutionType.EXECUTION;
    }


    private AuditEntryStatus getAuditStatus(AuditItem auditable) {
        switch (auditable.getOperation()) {
            case EXECUTION:
                return auditable.getRuntimeContext().isSuccess() ? AuditEntryStatus.EXECUTED : AuditEntryStatus.FAILED;
            case ROLLBACK:
            default:
                return auditable.getRuntimeContext().isSuccess() ? AuditEntryStatus.ROLLED_BACK : AuditEntryStatus.ROLLBACK_FAILED;
        }
    }
}
