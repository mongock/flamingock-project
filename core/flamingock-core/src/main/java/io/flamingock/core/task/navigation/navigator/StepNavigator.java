package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.writer.AuditItem;
import io.flamingock.core.audit.writer.RuntimeContext;
import io.flamingock.core.stage.executor.StageExecutionContext;
import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.RollableFailedStep;
import io.flamingock.core.task.navigation.step.TaskStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.afteraudit.RollableStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.CompletedSuccessStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompleteAutoRolledBackStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.execution.FailedExecutionStep;
import io.flamingock.core.task.navigation.step.execution.SuccessExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.FailedManualRolledBackStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.task.navigation.summary.StepSummarizer;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.Result;
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

    public final StepNavigationOutput executeTask(ExecutableTask task, StageExecutionContext stageExecutionContext) {
        if (task.isInitialExecutionRequired()) {

            // Main execution
            TaskStep executedStep = transactionWrapper != null && task.getDescriptor().isTransactional()
                    ? executeWithinTransaction(task, stageExecutionContext, runtimeManager)
                    : auditExecution(executeTask(task), stageExecutionContext, LocalDateTime.now());


            return executedStep instanceof RollableFailedStep
                    ? rollback((RollableFailedStep) executedStep, stageExecutionContext)
                    : new StepNavigationOutput(true, summarizer.getSummary());

        } else {
            //Task already executed
            logger.info("IGNORED - {}", task.getDescriptor().getId());
            summarizer.add(new CompletedAlreadyAppliedStep(task));
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    private TaskStep executeWithinTransaction(ExecutableTask task, StageExecutionContext stageExecutionContext, DependencyInjectable dependencyInjectable) {
        return transactionWrapper.wrapInTransaction(task.getDescriptor(), dependencyInjectable, () -> {
            ExecutionStep executed = executeTask(task);
            if (executed instanceof SuccessExecutionStep) {
                AfterExecutionAuditStep afterExecutionAuditStep = auditExecution(executed, stageExecutionContext, LocalDateTime.now());
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

    private AfterExecutionAuditStep auditExecution(ExecutionStep executionStep, StageExecutionContext stageExecutionContext, LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(executionStep).setExecutedAt(executedAt).build();

        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.EXECUTION, executionStep.getTaskDescriptor(), stageExecutionContext, runtimeContext));
        logAuditResult(auditResult, executionStep.getTaskDescriptor().getId(), "EXECUTION");
        AfterExecutionAuditStep afterExecutionAudit = executionStep.applyAuditResult(auditResult);
        summarizer.add(afterExecutionAudit);
        return afterExecutionAudit;
    }

    private StepNavigationOutput rollback(RollableFailedStep rollableFailedStep, StageExecutionContext stageExecutionContext) {
        if (rollableFailedStep instanceof CompleteAutoRolledBackStep) {
            //It's autoRollable(handled by the database engine or similar)
            summarizer.add((CompleteAutoRolledBackStep) rollableFailedStep);
        }
        rollableFailedStep.getRollbackSteps().forEach(rollableStep -> {
            ManualRolledBackStep rolledBack = manualRollback(rollableStep);
            auditManualRollback(rolledBack, stageExecutionContext, LocalDateTime.now());
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

    private void auditManualRollback(ManualRolledBackStep rolledBackStep, StageExecutionContext stageExecutionContext, LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(rolledBackStep).setExecutedAt(executedAt).build();
        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.ROLLBACK, rolledBackStep.getTaskDescriptor(), stageExecutionContext, runtimeContext));
        logAuditResult(auditResult, rolledBackStep.getTaskDescriptor().getId(), "ROLLBACK");
        CompletedFailedManualRollback failedStep = rolledBackStep.applyAuditResult(auditResult);
        summarizer.add(failedStep);
    }
}
