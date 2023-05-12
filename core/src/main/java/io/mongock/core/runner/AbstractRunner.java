package io.mongock.core.runner;

import io.mongock.api.exception.CoreException;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.event.EventPublisher;
import io.mongock.core.event.result.MigrationIgnoredResult;
import io.mongock.core.event.result.MigrationSuccessResult;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.executor.ProcessExecutionException;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.lock.Lock;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.lock.LockCheckException;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.runtime.RuntimeOrchestrator;
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
    private final RuntimeOrchestrator.Generator runtimeBuilder;


    public AbstractRunner(LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> lockAcquirer,
                          AuditReader<AUDIT_PROCESS_STATE> auditReader,
                          ProcessExecutor<EXECUTABLE_PROCESS> processExecutor,
                          ExecutionContext executionContext,
                          EventPublisher eventPublisher,
                          RuntimeOrchestrator.Generator runtimeBuilder,
                          boolean throwExceptionIfCannotObtainLock) {
        this.lockProvider = lockAcquirer;
        this.stateFetcher = auditReader;
        this.processExecutor = processExecutor;
        this.executionContext = executionContext;
        this.eventPublisher = eventPublisher;
        this.runtimeBuilder = runtimeBuilder;
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

        RuntimeOrchestrator runtimeHelper = runtimeBuilder.setLock(lock).generate();
        ProcessExecutor.Output executionOutput = processExecutor.run(executableProcess, executionContext, runtimeHelper);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(executionOutput));
    }

    private void skipProcess() {
        logger.info("Skipping the process. All the tasks are already executed.");
        eventPublisher.publishMigrationSuccessEvent(new MigrationIgnoredResult());
    }
}
