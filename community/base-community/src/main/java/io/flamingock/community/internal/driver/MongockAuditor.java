package io.flamingock.community.internal.driver;

import io.flamingock.community.internal.persistence.MongockAuditEntry;
import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.audit.writer.AbstractAuditWriter;
import io.flamingock.core.audit.writer.AuditItem;
import io.flamingock.core.audit.writer.RuntimeContext;
import io.flamingock.core.stage.executor.StageExecutionContext;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.util.ThrowableUtil;

public abstract class MongockAuditor
        extends AbstractAuditWriter<MongockAuditEntry>
        implements AuditWriter, SingleAuditReader {

    @Override
    protected final MongockAuditEntry map(AuditItem auditItem) {
        TaskDescriptor taskDescriptor = auditItem.getTaskDescriptor();
        StageExecutionContext stageExecutionContext = auditItem.getExecutionContext();
        RuntimeContext runtimeContext = auditItem.getRuntimeContext();
        return new MongockAuditEntry(
                stageExecutionContext.getExecutionId(),
                taskDescriptor.getId(),
                stageExecutionContext.getAuthor(),
                runtimeContext.getExecutedAt(),
                getAuditStatus(auditItem),
                getExecutionType(auditItem),
                taskDescriptor.getClassImplementor(),
                runtimeContext.getMethodExecutor(),
                runtimeContext.getDuration(),
                stageExecutionContext.getHostname(),
                stageExecutionContext.getMetadata(),
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
