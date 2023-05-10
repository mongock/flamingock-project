package io.mongock.core.execution.navigator;

import io.mongock.core.util.Result;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.execution.step.afteraudit.RollableStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.rolledback.FailedRolledBackStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator extends AbstractStepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);


    StepNavigator(AuditWriter<?> auditWriter,
                  StepSummarizer summarizer,
                  RuntimeHelper runtimeHelper) {
        super(auditWriter, summarizer, runtimeHelper);
    }

    protected StepNavigationOutput startNavigation(ExecutableTask task, ExecutionContext executionContext) {
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
        Result auditResult = auditWriter.writeStep(
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
}
