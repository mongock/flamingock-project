package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.execution.step.afteraudit.RollableStep;
import io.mongock.core.execution.step.complete.CompletedAlreadyAppliedStep;
import io.mongock.core.execution.step.complete.CompletedSuccessStep;
import io.mongock.core.execution.step.complete.failed.CompleteAutoRolledBackStep;
import io.mongock.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.execution.step.rolledback.FailedManualRolledBackStep;
import io.mongock.core.execution.step.rolledback.ManualRolledBackStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeOrchestrator;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;
import io.mongock.core.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    private StepSummarizer summarizer;
    private AuditWriter<?> auditWriter;

    private RuntimeOrchestrator runtimeHelper;

    private TransactionWrapper transactionWrapper;

    StepNavigator(AuditWriter<?> auditWriter,
                  StepSummarizer summarizer,
                  RuntimeOrchestrator runtimeHelper,
                  TransactionWrapper transactionWrapper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeHelper = runtimeHelper;
        this.transactionWrapper = transactionWrapper;
    }

    void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeHelper = null;
    }

    void setSummarizer(StepSummarizer summarizer) {
        this.summarizer = summarizer;
    }

    void setAuditWriter(AuditWriter<?> auditWriter) {
        this.auditWriter = auditWriter;
    }

    void setRuntimeHelper(RuntimeOrchestrator runtimeHelper) {
        this.runtimeHelper = runtimeHelper;
    }

    void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }

    public final StepNavigationOutput start(ExecutableTask task, ExecutionContext executionContext) {
        if (task.isInitialExecutionRequired()) {
            TaskStep afterExecution = transactionWrapper != null
                    ? executeTaskWrapped(task, executionContext)
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

    private TaskStep executeTaskWrapped(ExecutableTask task, ExecutionContext executionContext) {
        return transactionWrapper.wrapInTransaction(task.getDescriptor(), () -> {
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
        ExecutionStep executed = new ExecutableStep(task).execute(runtimeHelper);
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
            ManualRolledBackStep rolledBack = rollable.rollback(runtimeHelper);
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
