package io.flamingock.core.runner;

import io.flamingock.core.api.exception.CoreException;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.event.result.MigrationIgnoredResult;
import io.flamingock.core.event.result.MigrationSuccessResult;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.lock.LockAcquirer;
import io.flamingock.core.lock.LockAcquisition;
import io.flamingock.core.lock.LockException;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.pipeline.execution.StageExecutionException;
import io.flamingock.core.pipeline.execution.StageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRunner implements Runner {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRunner.class);

    private final LockAcquirer lockAcquirer;

    private final SingleAuditReader auditReader;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;

    private final StageExecutor stageExecutor;

    private final StageExecutionContext stageExecutionContext;


    public AbstractRunner(LockAcquirer lockAcquirer,
                          SingleAuditReader auditReader,
                          StageExecutor stageExecutor,
                          StageExecutionContext stageExecutionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock) {
        this.lockAcquirer = lockAcquirer;
        this.auditReader = auditReader;
        this.stageExecutor = stageExecutor;
        this.stageExecutionContext = stageExecutionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void run(Pipeline pipeline) throws CoreException {
        eventPublisher.publishMigrationStarted();//TODO change name to eventPublisher.publishPipelineStarted();
        pipeline.getStages().forEach(this::runStage);

    }

    private void runStage(Stage stage) {
        //TODO eventPublisher.publishStageStarted();
        LoadedStage loadedStage = stage.load();
        try (LockAcquisition lockAcquisition = lockAcquirer.acquireIfRequired(loadedStage)) {
            if(lockAcquisition.isNotRequired()) {
                skipStage();
            } else if(lockAcquisition.lock().isPresent()) {
                startStage(lockAcquisition.lock().get(), loadedStage);
            } else {
                throw new LockException("Lock required but not acquired");
            }

        } catch (LockException exception) {
            eventPublisher.publishMigrationFailedEvent(exception);
            if (throwExceptionIfCannotObtainLock) {
                logger.error("Required process lock not acquired. ABORTED OPERATION", exception);
                throw exception;

            } else {
                logger.warn("Process lock not acquired and `throwExceptionIfCannotObtainLock == false`.\n" + "If the application should abort, make `throwExceptionIfCannotObtainLock == true`\n" + "CONTINUING THE APPLICATION WITHOUT FINISHING THE PROCESS", exception);
            }

        } catch (StageExecutionException stageExecutionException) {
            logger.info("Process summary\n{}", stageExecutionException.getSummary().getPretty());
            eventPublisher.publishMigrationFailedEvent(stageExecutionException);
            throw stageExecutionException;
        } catch (Exception exception) {
            CoreException coreEx = exception instanceof CoreException ? (CoreException) exception : new CoreException(exception);
            logger.error("Error executing the process. ABORTED OPERATION", coreEx);
            eventPublisher.publishMigrationFailedEvent(coreEx);
            throw coreEx;
        }
    }

    private void startStage(Lock lock, LoadedStage loadedStage) throws StageExecutionException {
        SingleAuditStageStatus currentAuditStageStatus = auditReader.getAuditStageStatus();
        logger.debug("Pulled remote state:\n{}", currentAuditStageStatus);

        ExecutableStage executableStage = loadedStage.applyState(currentAuditStageStatus);
        logger.debug("Applied state to process:\n{}", executableStage);

        StageExecutor.Output executionOutput = stageExecutor.execute(executableStage, stageExecutionContext, lock);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(executionOutput));
    }

    private void skipStage() {
        logger.info("Skipping the process. All the tasks are already executed.");
        eventPublisher.publishMigrationSuccessEvent(new MigrationIgnoredResult());
    }
}
