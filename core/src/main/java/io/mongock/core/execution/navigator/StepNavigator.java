package io.mongock.core.execution.navigator;

import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.complete.CompleteSuccessStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.execution.step.afteraudit.RollableStep;
import io.mongock.core.execution.step.rolledback.FailedRolledBackStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.util.RuntimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    private static final StepNavigator instance = new StepNavigator();

    //For sequential execution
    public static StepNavigationOutput startByReuse(ExecutableTask task,
                                                    AuditWriter<?> auditWriter,
                                                    RuntimeHelper runtimeHelper,
                                                    ExecutionContext executionContext) {
        return instance.resetDependencies(auditWriter, new DefaultStepSummarizer(), runtimeHelper, executionContext)
                .start(task, executionContext);
    }

    //For parallel execution
    public static StepNavigationOutput startByNew(ExecutableTask task,
                                                  AuditWriter<?> auditWriter,
                                                  RuntimeHelper runtimeHelper,
                                                  ExecutionContext executionContext) {
        return new StepNavigator(auditWriter, new DefaultStepSummarizer(), runtimeHelper)
                .start(task, executionContext);
    }

    private StepSummarizer summarizer;
    private AuditWriter<?> auditWriter;

    private RuntimeHelper runtimeHelper;

    private StepNavigator() {
        this(null, null, null);
    }

    public StepNavigator(AuditWriter<?> auditWriter,
                         StepSummarizer summarizer,
                         RuntimeHelper runtimeHelper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeHelper = runtimeHelper;
    }

    private StepNavigator resetDependencies(AuditWriter<?> auditWriter,
                                            StepSummarizer summarizer,
                                            RuntimeHelper runtimeHelper,
                                            ExecutionContext executionContext) {
        clean();
        this.summarizer = summarizer;
        this.auditWriter = auditWriter;
        this.runtimeHelper = runtimeHelper;
        return this;
    }

    private void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeHelper = null;
    }

    private StepNavigationOutput start(ExecutableTask task, ExecutionContext executionContext) {
        if (task.isInitialExecutionRequired()) {
            ExecutionStep executed = executeAndSummary(task);

            AfterExecutionAuditStep afterAuditStep = auditExecutionAndSummary(
                    executed, executionContext, LocalDateTime.now());

            if (afterAuditStep instanceof FailedExecutionOrAuditStep) {
                //failed execution
                rollbackAndSummaryIfProvided((FailedExecutionOrAuditStep) afterAuditStep)
                        .ifPresent(rolledBackStep -> auditRollbackAndSummary(
                                rolledBackStep, executionContext, LocalDateTime.now()));
                return new StepNavigationOutput(false, summarizer.getSummary());

            } else {
                //successful execution
                return new StepNavigationOutput(true, summarizer.getSummary());

            }
        } else {
            //Task already executed
            logAndSummaryIgnored(task);
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    private ExecutionStep executeAndSummary(ExecutableTask task) {
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

    private AfterExecutionAuditStep auditExecutionAndSummary(ExecutionStep executionStep,
                                                             ExecutionContext executionContext,
                                                             LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder()
                .setTaskStep(executionStep)
                .setExecutedAt(executedAt)
                .build();

        AuditResult auditResult = auditWriter.writeStep(
                new AuditItem(
                        AuditItem.Operation.ROLLBACK,
                        executionStep.getTaskDescriptor(),
                        executionContext,
                        runtimeContext)
        );
        logAuditResult(auditResult, executionStep.getTaskDescriptor().getId(), "EXECUTION");
        AfterExecutionAuditStep attemptedSaveStateExecutionStep = executionStep.applyAuditResult(auditResult);
        summarizer.add(attemptedSaveStateExecutionStep);
        return attemptedSaveStateExecutionStep;
    }

    private Optional<RolledBackStep> rollbackAndSummaryIfProvided(FailedExecutionOrAuditStep failed) {
        if (failed.getRollableIfPresent().isPresent()) {
            RollableStep attemptedSaveStateExecutionStep = failed.getRollableIfPresent().get();
            RolledBackStep rolledBack = attemptedSaveStateExecutionStep.rollback(runtimeHelper);
            if (rolledBack instanceof FailedRolledBackStep) {
                logger.info("ROLL BACK FAILED - {} after {} ms",
                        rolledBack.getTask().getDescriptor().getId(),
                        rolledBack.getDuration());
                String msg = String.format("error rollback task[%s] after %d ms",
                        rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
                logger.error(msg, ((FailedRolledBackStep) rolledBack).getError());
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

    private void auditRollbackAndSummary(RolledBackStep rolledBackStep,
                                         ExecutionContext executionContext,
                                         LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder()
                .setTaskStep(rolledBackStep)
                .setExecutedAt(executedAt)
                .build();
        AuditResult auditResult = auditWriter.writeStep(
                new AuditItem(
                        AuditItem.Operation.ROLLBACK,
                        rolledBackStep.getTaskDescriptor(),
                        executionContext,
                        runtimeContext)
        );
        logAuditResult(auditResult, rolledBackStep.getTaskDescriptor().getId(), "ROLLBACK");
        CompleteFailedStep failedStep = rolledBackStep.applyAuditResult(auditResult);
        summarizer.add(failedStep);
    }

    private void logAndSummaryIgnored(ExecutableTask task) {
        logger.info("IGNORED - {}", task.getDescriptor().getId());
        summarizer.add(CompleteSuccessStep.fromTask(task));
    }

    private static void logAuditResult(AuditResult saveResult, String id, String operation) {
        if (saveResult instanceof AuditResult.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((AuditResult.Error) saveResult).getError()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }
}
