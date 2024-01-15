/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.cloud.transaction.OngoingStatusRepository;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.engine.audit.domain.RuntimeContext;
import io.flamingock.core.pipeline.execution.ExecutionContext;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyInjectable;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.navigation.step.ExecutableStep;
import io.flamingock.core.task.navigation.step.RollableFailedStep;
import io.flamingock.core.task.navigation.step.TaskStep;
import io.flamingock.core.task.navigation.step.afteraudit.AfterExecutionAuditStep;
import io.flamingock.core.task.navigation.step.afteraudit.RollableStep;
import io.flamingock.core.task.navigation.step.complete.CompletedAlreadyAppliedStep;
import io.flamingock.core.task.navigation.step.complete.CompletedSuccessStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompleteAutoRolledBackStep;
import io.flamingock.core.task.navigation.step.complete.failed.CompletedFailedManualRollback;
import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.execution.FailedExecutionStep;
import io.flamingock.core.task.navigation.step.execution.SuccessExecutionStep;
import io.flamingock.core.task.navigation.step.rolledback.FailedManualRolledBackStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.task.navigation.summary.StepSummarizer;
import io.flamingock.core.transaction.TransactionWrapper;
import io.flamingock.core.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StepNavigator {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);
    private StepSummarizer summarizer;
    private AuditWriter auditWriter;

    private RuntimeManager runtimeManager;

    private TransactionWrapper transactionWrapper;

    StepNavigator(AuditWriter auditWriter, StepSummarizer summarizer, RuntimeManager runtimeManager, TransactionWrapper transactionWrapper) {
        this.auditWriter = auditWriter;
        this.summarizer = summarizer;
        this.runtimeManager = runtimeManager;
        this.transactionWrapper = transactionWrapper;
    }

    private static void logAuditResult(Result saveResult, String id, String operation) {
        if (saveResult instanceof Result.Error) {
            logger.info("FAILED AUDIT " + operation + " TASK - {}\n{}", id, (((Result.Error) saveResult).getError().getLocalizedMessage()));
        } else {
            logger.info("SUCCESS AUDIT " + operation + " TASK - {}", id);
        }
    }

    void clean() {
        summarizer = null;
        auditWriter = null;
        runtimeManager = null;
    }

    void setSummarizer(StepSummarizer summarizer) {
        this.summarizer = summarizer;
    }

    void setAuditWriter(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    void setTransactionWrapper(TransactionWrapper transactionWrapper) {
        this.transactionWrapper = transactionWrapper;
    }

    public final StepNavigationOutput executeTask(ExecutableTask task, ExecutionContext stageExecutionContext) {
        if (task.isInitialExecutionRequired()) {

            // Main execution
            TaskStep executedStep = transactionWrapper != null && task.getDescriptor().isTransactional()
                    ? executeWithinTransaction(task, stageExecutionContext, runtimeManager)
                    : performAuditExecution(executeTask(task), stageExecutionContext, LocalDateTime.now());


            return executedStep instanceof RollableFailedStep
                    ? rollback((RollableFailedStep) executedStep, stageExecutionContext)
                    : new StepNavigationOutput(true, summarizer.getSummary());

        } else {
            //Task already executed
            logger.info("IGNORED - {}", task.getDescriptor().getId());
            summarizer.add(new CompletedAlreadyAppliedStep(task));
            return new StepNavigationOutput(true, summarizer.getSummary());
        }
    }

    private TaskStep executeWithinTransaction(ExecutableTask task,
                                              ExecutionContext stageExecutionContext,
                                              DependencyInjectable dependencyInjectable) {
        //If it's a cloud transaction, it requires to write the status
        getOngoingRepositoryIfCloudTransaction().ifPresent(ongoingRepo -> ongoingRepo.setOngoingExecution(task));

        return transactionWrapper.wrapInTransaction(task.getDescriptor(), dependencyInjectable, () -> {
            ExecutionStep executed = executeTask(task);
            if (executed instanceof SuccessExecutionStep) {
                AfterExecutionAuditStep executionAuditResult = performAuditExecution(executed, stageExecutionContext, LocalDateTime.now());
                if (executionAuditResult instanceof CompletedSuccessStep) {
                    //If it's a cloud transaction, it requires to clean the status
                    getOngoingRepositoryIfCloudTransaction()
                            .ifPresent(ongoingRepo -> ongoingRepo.cleanOngoingStatus(task.getDescriptor().getId()));
                    return executionAuditResult;
                }
            }
            //if it goes through here, it's failed, and it will be rolled back
            return new CompleteAutoRolledBackStep(task, true);
        });
    }

    private Optional<OngoingStatusRepository> getOngoingRepositoryIfCloudTransaction() {
        return transactionWrapper != null && CloudTransactioner.class.isAssignableFrom(transactionWrapper.getClass())
                ? Optional.of(((OngoingStatusRepository) transactionWrapper))
                : Optional.empty();
    }

    private ExecutionStep executeTask(ExecutableTask task) {
        ExecutionStep executed = new ExecutableStep(task).execute(runtimeManager);
        summarizer.add(executed);
        if (executed instanceof FailedExecutionStep) {
            FailedExecutionStep failed = (FailedExecutionStep) executed;
            logger.info("FAILED - " + executed.getTask().getDescriptor().getId());
            String msg = String.format("error execution task[%s] after %d ms", failed.getTask().getDescriptor().getId(), failed.getDuration());
            logger.warn(msg, failed.getError());

        } else {
            logger.info("APPLIED - {} after {} ms", executed.getTask().getDescriptor().getId(), executed.getDuration());
        }
        return executed;
    }

    private AfterExecutionAuditStep performAuditExecution(ExecutionStep executionStep,
                                                          ExecutionContext stageExecutionContext,
                                                          LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(executionStep).setExecutedAt(executedAt).build();

        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.EXECUTION, executionStep.getTaskDescriptor(), stageExecutionContext, runtimeContext));
        logAuditResult(auditResult, executionStep.getTaskDescriptor().getId(), "EXECUTION");
        AfterExecutionAuditStep afterExecutionAudit = executionStep.applyAuditResult(auditResult);
        summarizer.add(afterExecutionAudit);
        return afterExecutionAudit;
    }

    private StepNavigationOutput rollback(RollableFailedStep rollableFailedStep, ExecutionContext stageExecutionContext) {
        if (rollableFailedStep instanceof CompleteAutoRolledBackStep) {
            //It's autoRollable(handled by the database engine or similar)
            summarizer.add((CompleteAutoRolledBackStep) rollableFailedStep);
        }
        rollableFailedStep.getRollbackSteps().forEach(rollableStep -> {
            ManualRolledBackStep rolledBack = manualRollback(rollableStep);
            auditManualRollback(rolledBack, stageExecutionContext, LocalDateTime.now());
        });

        return new StepNavigationOutput(false, summarizer.getSummary());
    }

    private ManualRolledBackStep manualRollback(RollableStep rollable) {
        ManualRolledBackStep rolledBack = rollable.rollback(runtimeManager);
        if (rolledBack instanceof FailedManualRolledBackStep) {
            logger.info("ROLL BACK FAILED - {} after {} ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
            String msg = String.format("error rollback task[%s] after %d ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
            logger.error(msg, ((FailedManualRolledBackStep) rolledBack).getError());

        } else {
            logger.info("ROLLED BACK - {} after {} ms", rolledBack.getTask().getDescriptor().getId(), rolledBack.getDuration());
        }

        summarizer.add(rolledBack);
        return rolledBack;
    }

    private void auditManualRollback(ManualRolledBackStep rolledBackStep, ExecutionContext stageExecutionContext, LocalDateTime executedAt) {
        RuntimeContext runtimeContext = RuntimeContext.builder().setTaskStep(rolledBackStep).setExecutedAt(executedAt).build();
        Result auditResult = auditWriter.writeStep(new AuditItem(AuditItem.Operation.ROLLBACK, rolledBackStep.getTaskDescriptor(), stageExecutionContext, runtimeContext));
        logAuditResult(auditResult, rolledBackStep.getTaskDescriptor().getId(), "ROLLBACK");
        CompletedFailedManualRollback failedStep = rolledBackStep.applyAuditResult(auditResult);
        summarizer.add(failedStep);
    }
}
