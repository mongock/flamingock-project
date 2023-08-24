package io.flamingock.core.core.runner;

import io.flamingock.core.api.exception.CoreException;
import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.audit.single.SingleAuditReader;
import io.flamingock.core.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.event.result.MigrationIgnoredResult;
import io.flamingock.core.core.event.result.MigrationSuccessResult;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.execution.executor.ProcessExecutionException;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.execution.executor.SeqSingleProcessExecutor;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.lock.LockAcquisition;
import io.flamingock.core.core.lock.LockException;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.LoadedStage;
import io.flamingock.core.core.stage.StageDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRunner<AUDIT_STAGE_STATUS extends AuditStageStatus, EXECUTABLE_STAGE extends ExecutableStage>
        implements Runner {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRunner.class);

    private final LockAcquirer<AUDIT_STAGE_STATUS, EXECUTABLE_STAGE> lockProvider;

    private final SingleAuditReader auditReader;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;
    private final SeqSingleProcessExecutor processExecutor;
    private final ExecutionContext executionContext;


    public AbstractRunner(LockAcquirer<AUDIT_STAGE_STATUS, EXECUTABLE_STAGE> lockAcquirer,
                          SingleAuditReader auditReader,
                          SeqSingleProcessExecutor processExecutor,
                          ExecutionContext executionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock) {
        this.lockProvider = lockAcquirer;
        this.auditReader = auditReader;
        this.processExecutor = processExecutor;
        this.executionContext = executionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void execute(StageDefinition processDefinition) throws CoreException {
        eventPublisher.publishMigrationStarted();

        LoadedStage loadedStage = processDefinition.load();

        try (LockAcquisition lockAcquisition = lockProvider.acquireIfRequired(loadedStage)) {

            if(lockAcquisition.isRequired()) {
                if(lockAcquisition.isAcquired()) {
                    startProcess(((LockAcquisition.Acquired) lockAcquisition).getLock(), loadedStage);
                } else {
                    throw new LockException("Lock required but not acquired");
                }
            } else {
                skipProcess();
            }

        } catch (LockException exception) {
            eventPublisher.publishMigrationFailedEvent(exception);
            if (throwExceptionIfCannotObtainLock) {
                logger.error("Required process lock not acquired. ABORTED OPERATION", exception);
                throw exception;

            } else {
                logger.warn("Process lock not acquired and `throwExceptionIfCannotObtainLock == false`.\n" + "If the application should abort, make `throwExceptionIfCannotObtainLock == true`\n" + "CONTINUING THE APPLICATION WITHOUT FINISHING THE PROCESS", exception);
            }

        } catch (ProcessExecutionException processException) {
            logger.info("Process summary\n{}", processException.getSummary().getPretty());
            eventPublisher.publishMigrationFailedEvent(processException);
            throw processException;
        } catch (Exception exception) {
            CoreException coreEx = exception instanceof CoreException ? (CoreException) exception : new CoreException(exception);
            logger.error("Error executing the process. ABORTED OPERATION", coreEx);
            eventPublisher.publishMigrationFailedEvent(coreEx);
            throw coreEx;
        }
    }

    private void startProcess(Lock lock, LoadedStage loadedStage) throws ProcessExecutionException {
        SingleAuditStageStatus currentAuditProcessStatus = auditReader.getAuditProcessStatus();
        logger.debug("Pulled remote state:\n{}", currentAuditProcessStatus);

        ExecutableStage executableStage = loadedStage.applyState(currentAuditProcessStatus);
        logger.debug("Applied state to process:\n{}", executableStage);

        ProcessExecutor.Output executionOutput = processExecutor.run(executableStage, executionContext, lock);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(executionOutput));
    }

    private void skipProcess() {
        logger.info("Skipping the process. All the tasks are already executed.");
        eventPublisher.publishMigrationSuccessEvent(new MigrationIgnoredResult());
    }
}
