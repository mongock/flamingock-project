package io.mongock.core.runner;

import io.mongock.api.exception.CoreException;
import io.mongock.core.audit.AuditReader;
import io.mongock.core.audit.domain.AuditProcessStatus;
import io.mongock.core.dependency.DependencyManager;
import io.mongock.core.event.EventPublisher;
import io.mongock.core.event.result.MigrationIgnoredResult;
import io.mongock.core.event.result.MigrationSuccessResult;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.lock.Lock;
import io.mongock.core.lock.LockAcquirer;
import io.mongock.core.lock.LockCheckException;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.ExecutableProcess;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.util.RuntimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnerOrchestrator<AUDIT_PROCESS_STATE extends AuditProcessStatus, EXECUTABLE_PROCESS extends ExecutableProcess> {

    private static final Logger logger = LoggerFactory.getLogger(RunnerOrchestrator.class);

    private final LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> lockProvider;

    private final AuditReader<AUDIT_PROCESS_STATE> stateFetcher;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;
    private final ProcessExecutor<EXECUTABLE_PROCESS> processExecutor;
    private final ExecutionContext executionContext;
    private final DependencyManager dependencyManager;

    public RunnerOrchestrator(LockAcquirer<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> lockProvider,
                              AuditReader<AUDIT_PROCESS_STATE> stateFetcher,
                              ProcessExecutor<EXECUTABLE_PROCESS> processExecutor,
                              ExecutionContext executionContext,
                              EventPublisher eventPublisher,
                              DependencyManager dependencyManager,
                              boolean throwExceptionIfCannotObtainLock) {
        this.lockProvider = lockProvider;
        this.stateFetcher = stateFetcher;
        this.processExecutor = processExecutor;
        this.executionContext = executionContext;
        this.eventPublisher = eventPublisher;
        this.dependencyManager = dependencyManager;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }

    public void execute(DefinitionProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> processDefinition) throws CoreException {
        eventPublisher.publishMigrationStarted();

        LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> loadedProcess = processDefinition.load();

        try (Lock lock = lockProvider.acquireIfRequired(loadedProcess)) {
            switch (lock.getStatus()) {
                case ACQUIRED:
                    startProcess(loadedProcess);
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

        } catch (Exception ex) {
            CoreException coreEx = ex instanceof CoreException ? (CoreException) ex : new CoreException(ex);
            logger.error("Error executing the process. ABORTED OPERATION", coreEx);
            eventPublisher.publishMigrationFailedEvent(coreEx);
            throw coreEx;
        }
    }

    private void startProcess(LoadedProcess<AUDIT_PROCESS_STATE, EXECUTABLE_PROCESS> process) {
        AUDIT_PROCESS_STATE processCurrentState = stateFetcher.getAuditProcessStatus();
        logger.debug("Pulled remote state:\n{}", processCurrentState);

        EXECUTABLE_PROCESS executableProcess = process.applyState(processCurrentState);
        logger.debug("Applied state to process:\n{}", executableProcess);

        RuntimeHelper runtimeHelper = new RuntimeHelper(dependencyManager);
        ProcessExecutor.Output executionOutput = processExecutor.run(executableProcess, executionContext, runtimeHelper);
        logger.info("Finished process successfully\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(executionOutput));
    }

    private void skipProcess() {
        logger.info("Skipping the process. All the tasks are already executed.");
        eventPublisher.publishMigrationSuccessEvent(new MigrationIgnoredResult());
    }
}
