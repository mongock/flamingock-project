package io.flamingock.core.audit.domain;

import io.flamingock.core.audit.writer.AuditItem;
import io.flamingock.core.audit.writer.RuntimeContext;
import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.util.ThrowableUtil;

public final class AuditEntryMapper {

    private AuditEntryMapper(){
    }

    public static  AuditEntry map(AuditItem auditItem) {
        TaskDescriptor taskDescriptor = auditItem.getTaskDescriptor();
        StageExecutionContext stageExecutionContext = auditItem.getExecutionContext();
        RuntimeContext runtimeContext = auditItem.getRuntimeContext();
        return new AuditEntry(
                stageExecutionContext.getExecutionId(),
                taskDescriptor.getId(),
                stageExecutionContext.getAuthor(),
                runtimeContext.getExecutedAt(),
                getAuditStatus(auditItem),
                getExecutionType(auditItem),
                taskDescriptor.getSourceName(),
                runtimeContext.getMethodExecutor(),
                runtimeContext.getDuration(),
                stageExecutionContext.getHostname(),
                stageExecutionContext.getMetadata(),
                getSystemChange(auditItem),
                ThrowableUtil.serialize(runtimeContext.getError().orElse(null))
        );
    }

    private static AuditEntryStatus getAuditStatus(AuditItem auditable) {
        switch (auditable.getOperation()) {
            case EXECUTION:
                return auditable.getRuntimeContext().isSuccess() ? AuditEntryStatus.EXECUTED : AuditEntryStatus.FAILED;
            case ROLLBACK:
            default:
                return auditable.getRuntimeContext().isSuccess() ? AuditEntryStatus.ROLLED_BACK : AuditEntryStatus.ROLLBACK_FAILED;
        }
    }

    private static AuditEntry.ExecutionType getExecutionType(AuditItem auditItem) {
        return AuditEntry.ExecutionType.EXECUTION;
    }

    private static boolean getSystemChange(AuditItem auditItem) {
        return false;
    }

}
