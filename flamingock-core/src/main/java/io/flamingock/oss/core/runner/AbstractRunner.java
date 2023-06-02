package io.flamingock.oss.core.runner;

import io.flamingock.oss.core.process.DefinitionProcess;
import io.flamingock.oss.core.process.ExecutableProcess;
import io.flamingock.oss.api.exception.CoreException;
import io.flamingock.oss.core.audit.AuditReader;
import io.flamingock.oss.core.audit.domain.AuditProcessStatus;
import io.flamingock.oss.core.event.EventPublisher;
import io.flamingock.oss.core.event.result.MigrationIgnoredResult;
import io.flamingock.oss.core.event.result.MigrationSuccessResult;
import io.flamingock.oss.core.execution.executor.ExecutionContext;
import io.flamingock.oss.core.execution.executor.ProcessExecutionException;
import io.flamingock.oss.core.execution.executor.ProcessExecutor;
import io.flamingock.oss.core.lock.Lock;
import io.flamingock.oss.core.lock.LockAcquirer;
import io.flamingock.oss.core.lock.LockCheckException;
import io.flamingock.oss.core.process.LoadedProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRunner<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess>
implements Runner{

    private static final Logger logger = LoggerFactory.getLogger(AbstractRunner.class);

    private final LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> lockProvider;

    private final AuditReader<AUDIT_PROCESS_STATE> stateFetcher;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;
    private final ProcessExecutor<EXECUTABLE_PROCESS> processExecutor;
    private final ExecutionContext executionContext;


    public AbstractRunner(LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> lockAcquirer,
                          AuditReader<AUDIT_PROCESS_STATE> auditReader,
                          ProcessExecutor<EXECUTABLE_PROCESS> processExecutor,
                          ExecutionContext executionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock) {
        this.lockProvider = lockAcquirer;
        this.stateFetcher = auditReader;
        this.processExecutor = processExecutor;
        this.executionContext = executionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void execute(DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> processDefinition) throws CoreException {
        eventPublisher.publishMigrationStarted();

        LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedProcess = processDefinition.load();

        try (Lock lock = lockProvider.acquireIfRequired(loadedProcess)) {
            switch (lock.getStatus()) {
                case ACQUIRED:
                    startProcess(lock, loadedProcess);
                    break;
                case NOT_REQUIRED:
                    skipProcess();
                    break;
            }
        } catch (LockCheckException exception) {
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

    private void startProcess(Lock lock, LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> process) throws ProcessExecutionException {
        AUDIT_PROCESS_STATE processCurrentState = stateFetcher.getAuditProcessStatus();
        logger.debug("Pulled remote state:\n{}", processCurrentState);

        EXECUTABLE_PROCESS executableProcess = process.applyState(processCurrentState);
        logger.debug("Applied state to process:\n{}", executableProcess);

        ProcessExecutor.Output executionOutput = processExecutor.run(executableProcess, executionContext, lock);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(executionOutput));
    }

    private void skipProcess() {
        logger.info("Skipping the process. All the tasks are already executed.");
        eventPublisher.publishMigrationSuccessEvent(new MigrationIgnoredResult());
    }
}
