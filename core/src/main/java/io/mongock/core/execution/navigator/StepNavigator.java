package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.audit.writer.RuntimeContext;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.TaskStep;
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
import io.mongock.core.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator extends AbstractStepNavigator {


    StepNavigator(AuditWriter<?> auditWriter,
                  StepSummarizer summarizer,
                  RuntimeHelper runtimeHelper) {
        super(auditWriter, summarizer, runtimeHelper);
    }



}
