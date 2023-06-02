package io.flamingock.oss.core.execution.navigator;

import io.flamingock.oss.core.audit.writer.AuditItem;
import io.flamingock.oss.core.audit.writer.AuditWriter;
import io.flamingock.oss.core.audit.writer.RuntimeContext;
import io.flamingock.oss.core.execution.executor.ExecutionContext;
import io.flamingock.oss.core.runtime.RuntimeManager;
import io.flamingock.oss.core.task.executable.ExecutableTask;
import io.flamingock.oss.core.util.Result;
import io.flamingock.oss.core.execution.step.ExecutableStep;
import io.flamingock.oss.core.execution.step.TaskStep;
import io.flamingock.oss.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.oss.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.flamingock.oss.core.execution.step.afteraudit.RollableStep;
import io.flamingock.oss.core.execution.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.oss.core.execution.step.complete.CompletedSuccessStep;
import io.flamingock.oss.core.execution.step.complete.failed.CompleteAutoRolledBackStep;
import io.flamingock.oss.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.oss.core.execution.step.execution.ExecutionStep;
import io.flamingock.oss.core.execution.step.execution.FailedExecutionStep;
import io.flamingock.oss.core.execution.step.execution.SuccessExecutionStep;
import io.flamingock.oss.core.execution.step.rolledback.FailedManualRolledBackStep;
import io.flamingock.oss.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.oss.core.execution.summary.StepSummarizer;
import io.flamingock.oss.core.runtime.dependency.DependencyInjector;
import io.flamingock.oss.core.transaction.TransactionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    private StepSummarizer summarizer;
    private AuditWriter<?> auditWriter;

    private RuntimeManager runtimeManager;

    private TransactionWrapper transactionWrapper;

    StepNavigator(AuditWriter<?> auditWriter,
                  StepSummarizer summarizer,
                  RuntimeManager runtimeManager,
                  TransactionWrapper transactionWrapper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeManager = runtimeManager;
        this.transactionWrapper = transactionWrapper;
    }

    void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeManager = null;
    }

    void setSummarizer(StepSummarizer summarizer) {
        this.summarizer = summarizer;
    }

    void setAuditWriter(AuditWriter<?> auditWriter) {
        this.auditWriter = auditWriter;
    }

    void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }

    public final StepNavigationOutput start(ExecutableTask task, ExecutionContext executionContext) {
        if (task.isInitialExecutionRequired()) {
            TaskStep afterExecution = transactionWrapper != null
                    ? executeTaskWrapped(task, executionContext, runtimeManager)
                    : executeTaskUnwrapped(task, executionContext);

            if (afterExecution instanceof CompleteAutoRolledBackStep) {
                summarizer.add((CompleteAutoRolledBackStep) afterExecution);
                return new StepNavigationOutput(false, summarizer.getSummary());

            } else if (afterExecution instanceof FailedExecutionOrAuditStep) {
                //failed execution
                manualRollback((FailedExecutionOrAuditStep) afterExecution).ifPresent(
                        rolledBackStep -> auditManualRollback(rolledBackStep, executionContext, LocalDateTime.now())
                );
                return new StepNavigationOutput(false, summarizer.getSummary());

            } else {
                //SUCCESSFUL EXECUTION
                return new StepNavigationOutput(true, summarizer.getSummary());

            }

        } else {
            //Task already executed
            logger.info("IGNORED - {}", task.getDescriptor().getId());
            summarizer.add(new CompletedAlreadyAppliedStep(task));
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    private TaskStep executeTaskWrapped(ExecutableTask task, ExecutionContext executionContext, DependencyInjector dependencyInjector) {
        return transactionWrapper.wrapInTransaction(task.getDescriptor(), dependencyInjector, () -> {
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

    private TaskStep executeTaskUnwrapped(ExecutableTask task, ExecutionContext executionContext) {
        ExecutionStep executed = executeTask(task);
        return auditExecution(executed, executionContext, LocalDateTime.now());
    }

    private ExecutionStep executeTask(ExecutableTask task) {
        ExecutionStep executed = new ExecutableStep(task).execute(runtimeManager);
        summarizer.add(executed);
        if (executed instanceof FailedExecutionStep) {
            FailedExecutionStep failed = (FailedExecutionStep) executed;
            logger.info("FAILED - " + executed.getTask().getDescriptor().getId());
            String msg = String.format("error execution task[%s] after %d ms",
                    failed.getTask().getDescriptor().getId(),
                    failed.getDuration());
            logger.warn(msg, failed.getError());

        } else {
            logger.info("APPLIED - {} after {} ms", executed.getTask().getDescriptor().getId(), executed.getDuration());
        }
        return executed;
    }

    private AfterExecutionAuditStep auditExecution(ExecutionStep executionStep,
                                                   ExecutionContext executionContext,
                                                   LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder()
                .setTaskStep(executionStep)
                .setExecutedAt(executedAt)
                .build();

        Result auditResult = auditWriter.writeStep(
                new AuditItem(
                        AuditItem.Operation.EXECUTION,
                        executionStep.getTaskDescriptor(),
                        executionContext,
                        runtimeContext)
        );
        logAuditResult(auditResult, executionStep.getTaskDescriptor().getId(), "EXECUTION");
        AfterExecutionAuditStep afterExecutionAudit = executionStep.applyAuditResult(auditResult);
        summarizer.add(afterExecutionAudit);
        return afterExecutionAudit;
    }

    private Optional<ManualRolledBackStep> manualRollback(FailedExecutionOrAuditStep failed) {
        if (failed.getRollableIfPresent().isPresent()) {
            RollableStep rollable = failed.getRollableIfPresent().get();
            ManualRolledBackStep rolledBack = rollable.rollback(runtimeManager);
            if (rolledBack instanceof FailedManualRolledBackStep) {
                logger.info("ROLL BACK FAILED - {} after {} ms",
                        rolledBack.getTask().getDescriptor().getId(),
                        rolledBack.getDuration());
                String msg = String.format("error rollback task[%s] after %d ms",
                        rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
                logger.error(msg, ((FailedManualRolledBackStep) rolledBack).getError());
            } else {
                logger.info("ROLLED BACK - {} after {} ms",
                        rolledBack.getTask().getDescriptor().getId(),
                        rolledBack.getDuration());
            }

            summarizer.add(rolledBack);
            return Optional.of(rolledBack);
        } else {
            logger.warn("ROLLBACK NOT PROVIDED FOR - {}", failed.getTask().getDescriptor().getId());
            return Optional.empty();
        }
    }

    private void auditManualRollback(ManualRolledBackStep rolledBackStep,
                                     ExecutionContext executionContext,
                                     LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder()
                .setTaskStep(rolledBackStep)
                .setExecutedAt(executedAt)
                .build();
        Result auditResult = auditWriter.writeStep(
                new AuditItem(
                        AuditItem.Operation.ROLLBACK,
                        rolledBackStep.getTaskDescriptor(),
                        executionContext,
                        runtimeContext)
        );
        logAuditResult(auditResult, rolledBackStep.getTaskDescriptor().getId(), "ROLLBACK");
        CompletedFailedManualRollback failedStep = rolledBackStep.applyAuditResult(auditResult);
        summarizer.add(failedStep);
    }


    private static void logAuditResult(Result saveResult, String id, String operation) {
        if (saveResult instanceof Result.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((Result.Error) saveResult).getError()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }
}