package io.mongock.core.execution.navigator;

import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.complete.CompletedSuccessStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.execution.step.complete.failed.CompleteAutoRolledBackStep;
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
            if (executed instanceof SuccessExecutionStep) {
                AfterExecutionAuditStep afterExecutionAuditStep = auditExecutionAndSummary(executed, executionContext, LocalDateTime.now());
                if (afterExecutionAuditStep instanceof CompletedSuccessStep) {
                    return afterExecutionAuditStep;
                }
            }
            //if it goes through here, it's failed, and it will be rolled back
            return new CompleteAutoRolledBackStep(task, true);
        });
    }


}
