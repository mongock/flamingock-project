package io.flamingock.oss.internal.driver;

import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.audit.writer.AbstractAuditWriter;
import io.flamingock.core.core.audit.writer.AuditItem;
import io.flamingock.core.core.audit.writer.RuntimeContext;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.util.ThrowableUtil;
import io.flamingock.oss.internal.persistence.MongockAuditEntry;

public abstract class MongockAuditor
        extends AbstractAuditWriter<MongockAuditEntry>
        implements AuditWriter, AuditReader<SingleAuditProcessStatus> {

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
