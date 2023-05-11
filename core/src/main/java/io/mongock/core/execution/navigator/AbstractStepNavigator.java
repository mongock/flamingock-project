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
import io.mongock.core.execution.step.complete.failed.CompletedFailedManualRollback;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.step.complete.failed.CompleteAutoRolledBackStep;
import io.mongock.core.execution.step.rolledback.FailedManualRolledBackStep;
import io.mongock.core.execution.step.rolledback.ManualRolledBackStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AbstractStepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractStepNavigator.class);

    protected StepSummarizer summarizer;
    protected AuditWriter<?> auditWriter;

    protected RuntimeHelper runtimeHelper;

    protected AbstractStepNavigator(AuditWriter<?> auditWriter,
                                    StepSummarizer summarizer,
                                    RuntimeHelper runtimeHelper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeHelper = runtimeHelper;
    }

    protected void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeHelper = null;
    }

    protected void setSummarizer(StepSummarizer summarizer) {
        this.summarizer = summarizer;
    }

    protected void setAuditWriter(AuditWriter<?> auditWriter) {
        this.auditWriter = auditWriter;
    }

    protected void setRuntimeHelper(RuntimeHelper runtimeHelper) {
        this.runtimeHelper = runtimeHelper;
    }

    public final StepNavigationOutput start(ExecutableTask task, ExecutionContext executionContext) {
        if (task.isInitialExecutionRequired()) {
            TaskStep afterExecution = startExecution(task, executionContext);

            if (afterExecution instanceof CompleteAutoRolledBackStep) {
                summarizer.add((CompleteAutoRolledBackStep) afterExecution);
                return new StepNavigationOutput(false, summarizer.getSummary());

            } else if (afterExecution instanceof FailedExecutionOrAuditStep) {
                //failed execution
                rollbackAndSummaryIfProvided((FailedExecutionOrAuditStep) afterExecution)
                        .ifPresent(rolledBackStep -> auditRollbackAndSummary(
                                rolledBackStep, executionContext, LocalDateTime.now()));
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

    protected TaskStep startExecution(ExecutableTask task, ExecutionContext executionContext) {
        ExecutionStep executed = executeAndSummary(task);
        return auditExecutionAndSummary(executed, executionContext, LocalDateTime.now());
    }

    protected final ExecutionStep executeAndSummary(ExecutableTask task) {
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

    protected final AfterExecutionAuditStep auditExecutionAndSummary(ExecutionStep executionStep,
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

    private Optional<ManualRolledBackStep> rollbackAndSummaryIfProvided(FailedExecutionOrAuditStep failed) {
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

    private void auditRollbackAndSummary(ManualRolledBackStep rolledBackStep,
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


    protected static void logAuditResult(Result saveResult, String id, String operation) {
        if (saveResult instanceof Result.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((Result.Error) saveResult).getError()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }
}
