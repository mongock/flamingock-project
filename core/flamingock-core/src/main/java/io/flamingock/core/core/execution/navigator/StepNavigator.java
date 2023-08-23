package io.flamingock.core.core.execution.navigator;

import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.audit.writer.AuditItem;
import io.flamingock.core.core.audit.writer.RuntimeContext;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.execution.step.ExecutableStep;
import io.flamingock.core.core.execution.step.RollableFailedStep;
import io.flamingock.core.core.execution.step.TaskStep;
import io.flamingock.core.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.core.execution.step.afteraudit.RollableStep;
import io.flamingock.core.core.execution.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.core.execution.step.complete.CompletedSuccessStep;
import io.flamingock.core.core.execution.step.complete.failed.CompleteAutoRolledBackStep;
import io.flamingock.core.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.core.execution.step.execution.ExecutionStep;
import io.flamingock.core.core.execution.step.execution.FailedExecutionStep;
import io.flamingock.core.core.execution.step.execution.SuccessExecutionStep;
import io.flamingock.core.core.execution.step.rolledback.FailedManualRolledBackStep;
import io.flamingock.core.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.core.execution.summary.StepSummarizer;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.transaction.TransactionWrapper;
import io.flamingock.core.core.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class StepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);
    private StepSummarizer summarizer;
    private AuditWriter auditWriter;

    private RuntimeManager runtimeManager;

    private TransactionWrapper transactionWrapper;

    StepNavigator(AuditWriter auditWriter, StepSummarizer summarizer, RuntimeManager runtimeManager, TransactionWrapper transactionWrapper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeManager = runtimeManager;
        this.transactionWrapper = transactionWrapper;
    }

    private static void logAuditResult(Result saveResult, String id, String operation) {
        if (saveResult instanceof Result.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((Result.Error) saveResult).getError().getLocalizedMessage()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }

    void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeManager = null;
    }

    void setSummarizer(StepSummarizer summarizer) {
        this.summarizer = summarizer;
    }

    void setAuditWriter(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }

    public final StepNavigationOutput executeTask(ExecutableTask task, ExecutionContext executionContext) {
        if (task.isInitialExecutionRequired()) {

            // Main execution
            TaskStep executedStep = transactionWrapper != null && task.getDescriptor().isTransactional()
                    ? executeWithinTransaction(task, executionContext, runtimeManager)
                    : auditExecution(executeTask(task), executionContext, LocalDateTime.now());


            return executedStep instanceof RollableFailedStep
                    ? rollback((RollableFailedStep) executedStep, executionContext)
                    : new StepNavigationOutput(true, summarizer.getSummary());

        } else {
            //Task already executed
            logger.info("IGNORED - {}", task.getDescriptor().getId());
            summarizer.add(new CompletedAlreadyAppliedStep(task));
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    private TaskStep executeWithinTransaction(ExecutableTask task, ExecutionContext executionContext, DependencyInjectable dependencyInjectable) {
        return transactionWrapper.wrapInTransaction(task.getDescriptor(), dependencyInjectable, () -> {
            ExecutionStep executed = executeTask(task);
            if (executed instanceof SuccessExecutionStep) {
                AfterExecutionAuditStep afterExecutionAuditStep = auditExecution(executed, executionContext, LocalDateTime.now());
                if (afterExecutionAuditStep instanceof CompletedSuccessStep) {
                    return afterExecutionAuditStep;
                }
            }
            //if it goes through here, it's failed, and it will be rolled back
            return new CompleteAutoRolledBackStep(task, true);
        });
    }

    private ExecutionStep executeTask(ExecutableTask task) {
        ExecutionStep executed = new ExecutableStep(task).execute(runtimeManager);
        summarizer.add(executed);
        if (executed instanceof FailedExecutionStep) {
            FailedExecutionStep failed = (FailedExecutionStep) executed;
            logger.info("FAILED - " + executed.getTask().getDescriptor().getId());
            String msg = String.format("error execution task[%s] after %d ms", failed.getTask().getDescriptor().getId(), failed.getDuration());
            logger.warn(msg, failed.getError());

        } else {
            logger.info("APPLIED - {} after {} ms", executed.getTask().getDescriptor().getId(), executed.getDuration());
        }
        return executed;
    }

    private AfterExecutionAuditStep auditExecution(ExecutionStep executionStep, ExecutionContext executionContext, LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(executionStep).setExecutedAt(executedAt).build();

        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.EXECUTION, executionStep.getTaskDescriptor(), executionContext, runtimeContext));
        logAuditResult(auditResult, executionStep.getTaskDescriptor().getId(), "EXECUTION");
        AfterExecutionAuditStep afterExecutionAudit = executionStep.applyAuditResult(auditResult);
        summarizer.add(afterExecutionAudit);
        return afterExecutionAudit;
    }

    private StepNavigationOutput rollback(RollableFailedStep rollableFailedStep, ExecutionContext executionContext) {
        if (rollableFailedStep instanceof CompleteAutoRolledBackStep) {
            //It's autoRollable(handled by the database engine or similar)
            summarizer.add((CompleteAutoRolledBackStep) rollableFailedStep);
        }
        rollableFailedStep.getRollbackSteps().forEach(rollableStep -> {
            ManualRolledBackStep rolledBack = manualRollback(rollableStep);
            auditManualRollback(rolledBack, executionContext, LocalDateTime.now());
        });

        return new StepNavigationOutput(false, summarizer.getSummary());
    }

    private ManualRolledBackStep manualRollback(RollableStep rollable) {
        ManualRolledBackStep rolledBack = rollable.rollback(runtimeManager);
        if (rolledBack instanceof FailedManualRolledBackStep) {
            logger.info("ROLL BACK FAILED - {} after {} ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
            String msg = String.format("error rollback task[%s] after %d ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
            logger.error(msg, ((FailedManualRolledBackStep) rolledBack).getError());

        } else {
            logger.info("ROLLED BACK - {} after {} ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
        }

        summarizer.add(rolledBack);
        return rolledBack;
    }

    private void auditManualRollback(ManualRolledBackStep rolledBackStep, ExecutionContext executionContext, LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(rolledBackStep).setExecutedAt(executedAt).build();
        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.ROLLBACK, rolledBackStep.getTaskDescriptor(), executionContext, runtimeContext));
        logAuditResult(auditResult, rolledBackStep.getTaskDescriptor().getId(), "ROLLBACK");
        CompletedFailedManualRollback failedStep = rolledBackStep.applyAuditResult(auditResult);
        summarizer.add(failedStep);
    }
}
