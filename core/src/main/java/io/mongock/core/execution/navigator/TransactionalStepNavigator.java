package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.afteraudit.FailedExecutionOrAuditStep;
import io.mongock.core.execution.step.complete.CompleteFailedStep;
import io.mongock.core.execution.step.complete.SuccessCompleteStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;

import java.time.LocalDateTime;

public class TransactionalStepNavigator extends AbstractStepNavigator {


    private TransactionWrapper transactionWrapper;

    protected TransactionalStepNavigator(AuditWriter<?> auditWriter,
                                         StepSummarizer summarizer,
                                         RuntimeHelper runtimeHelper,
                                         TransactionWrapper transactionWrapper) {
        super(auditWriter, summarizer, runtimeHelper);
        this.transactionWrapper = transactionWrapper;
    }

    protected void clean() {
        super.clean();
        transactionWrapper = null;
    }

    protected void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }


    protected TaskStep startExecution(ExecutableTask task, ExecutionContext executionContext) {
        return transactionWrapper.wrapInTransaction(task.getDescriptor(), () -> {
            ExecutionStep executed = executeAndSummary(task);
            AfterExecutionAuditStep afterExecutionAuditStep = auditExecutionAndSummary(executed, executionContext, LocalDateTime.now());
            return afterExecutionAuditStep instanceof SuccessCompleteStep
                    ? afterExecutionAuditStep
                    : null;//TODO create CompleteFailedStep
        });
    }


}
