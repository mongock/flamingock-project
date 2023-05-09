package io.mongock.core.execution.navigator;

import io.mongock.api.exception.CoreException;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.step.afteraudit.AfterExecutionAuditStep;
import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.transaction.TransactionWrapper;

import java.time.LocalDateTime;

public class TransactionalStepNavigator extends AbstractStepNavigator {


    private TransactionWrapper transactionWrapper;

    protected TransactionalStepNavigator() {
        this(null, null, null);
    }

    protected TransactionalStepNavigator(AuditWriter<?> auditWriter,
                               StepSummarizer summarizer,
                               RuntimeHelper runtimeHelper) {
        super(auditWriter, summarizer, runtimeHelper);
    }

    protected void clean() {
        super.clean();
        transactionWrapper = null;
    }

    protected void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }

    protected StepNavigationOutput startNavigation(ExecutableTask task, ExecutionContext executionContext) {
        boolean result = transactionWrapper.wrapInTransaction(() -> {
            ExecutionStep executed = executeAndSummary(task);
            if (executed instanceof FailedExecutionStep) {
                throw new CoreException(((FailedExecutionStep) executed).getError());
            }
            auditExecutionAndSummary(executed, executionContext, LocalDateTime.now());

            AfterExecutionAuditStep resultStep = auditExecutionAndSummary(executed, executionContext, LocalDateTime.now());
            if (!resultStep.isSuccessStep()) {
                throw new CoreException("Error save audit log for task: " + resultStep.getTask().getDescriptor().getId());
            }
//            return resultStep;
        });
        return new StepNavigationOutput(result, summarizer.getSummary());
    }


}
