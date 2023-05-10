package io.mongock.core.execution.navigator;

import io.mongock.core.util.Result;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.AlreadyAppliedStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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
            return startNavigation(task, executionContext);
        } else {
            //Task already executed
            logger.info("IGNORED - {}", task.getDescriptor().getId());
            summarizer.add(new AlreadyAppliedStep(task));
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    protected abstract StepNavigationOutput startNavigation(ExecutableTask task, ExecutionContext executionContext);

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
        AfterExecutionAuditStep attemptedSaveStateExecutionStep = executionStep.applyAuditResult(auditResult);
        summarizer.add(attemptedSaveStateExecutionStep);
        return attemptedSaveStateExecutionStep;
    }


    protected static void logAuditResult(Result saveResult, String id, String operation) {
        if (saveResult instanceof Result.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((Result.Error) saveResult).getError()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }
}
