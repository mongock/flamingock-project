package io.flamingock.core.core.runner;

import io.flamingock.core.api.exception.CoreException;
import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.domain.AuditProcessStatus;
import io.flamingock.core.core.event.EventPublisher;
import io.flamingock.core.core.event.result.MigrationIgnoredResult;
import io.flamingock.core.core.event.result.MigrationSuccessResult;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.execution.executor.ProcessExecutionException;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockAcquirer;
import io.flamingock.core.core.lock.LockAcquisition;
import io.flamingock.core.core.lock.LockException;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.ExecutableProcess;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.filter.TaskFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class AbstractRunner<AUDIT_PROCESS_STATUS extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess>
        implements Runner {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRunner.class);

    private final LockAcquirer<AUDIT_PROCESS_STATUS, EXECUTABLE_PROCESS> lockProvider;

    private final AuditReader<AUDIT_PROCESS_STATUS> auditReader;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;
    private final ProcessExecutor<EXECUTABLE_PROCESS> processExecutor;
    private final ExecutionContext executionContext;
    private final Collection<TaskFilter<?>> filters;


    public AbstractRunner(LockAcquirer<AUDIT_PROCESS_STATUS, EXECUTABLE_PROCESS> lockAcquirer,
                          AuditReader<AUDIT_PROCESS_STATUS> auditReader,
                          ProcessExecutor<EXECUTABLE_PROCESS> processExecutor,
                          Collection<TaskFilter<?>> filters,
                          ExecutionContext executionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock) {
        this.lockProvider = lockAcquirer;
        this.auditReader = auditReader;
        this.processExecutor = processExecutor;
        this.filters = filters;
        this.executionContext = executionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void execute(DefinitionProcess<AUDIT_PROCESS_STATUS, EXECUTABLE_PROCESS> processDefinition) throws CoreException {
        eventPublisher.publishMigrationStarted();

        LoadedProcess<AUDIT_PROCESS_STATUS, EXECUTABLE_PROCESS> loadedProcess = processDefinition.load(filters);

        try (LockAcquisition lockAcquisition = lockProvider.acquireIfRequired(loadedProcess)) {
            if (lockAcquisition instanceof LockAcquisition.Acquired) {
                startProcess(((LockAcquisition.Acquired) lockAcquisition).getLock(), loadedProcess);
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

    private void startProcess(Lock lock, LoadedProcess<AUDIT_PROCESS_STATUS, EXECUTABLE_PROCESS> process) throws ProcessExecutionException {
        AUDIT_PROCESS_STATUS currentAuditProcessStatus = auditReader.getAuditProcessStatus();
        logger.debug("Pulled remote state:\n{}", currentAuditProcessStatus);

        EXECUTABLE_PROCESS executableProcess = process.applyState(currentAuditProcessStatus);
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
