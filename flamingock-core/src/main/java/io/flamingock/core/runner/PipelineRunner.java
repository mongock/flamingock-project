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

package io.flamingock.core.runner;

import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.engine.execution.Execution;
import io.flamingock.core.engine.execution.ExecutionPlanner;
import io.flamingock.core.engine.lock.Lock;
import io.flamingock.core.engine.lock.LockException;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.event.model.impl.PipelineCompletedEvent;
import io.flamingock.core.event.model.impl.PipelineFailedEvent;
import io.flamingock.core.event.model.impl.PipelineStartedEvent;
import io.flamingock.core.event.model.impl.StageCompletedEvent;
import io.flamingock.core.event.model.impl.StageFailedEvent;
import io.flamingock.core.event.model.impl.StageIgnoredEvent;
import io.flamingock.core.event.model.impl.StageStartedEvent;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.pipeline.execution.StageExecutionException;
import io.flamingock.core.pipeline.execution.StageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.flamingock.core.engine.execution.Execution.Action.EXECUTE;

public abstract class PipelineRunner implements Runner {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunner.class);

    private final ExecutionPlanner executionPlanner;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;

    private final StageExecutor stageExecutor;

    private final StageExecutionContext stageExecutionContext;


    public PipelineRunner(ExecutionPlanner executionPlanner,
                          StageExecutor stageExecutor,
                          StageExecutionContext stageExecutionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock) {
        this.executionPlanner = executionPlanner;
        this.stageExecutor = stageExecutor;
        this.stageExecutionContext = stageExecutionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    }


    public void run(Pipeline pipeline) throws FlamingockException {
        eventPublisher.publish(new PipelineStartedEvent());
        boolean keepLooping = true;
        do {
            try(Execution execution = executionPlanner.getNextExecution(pipeline)) {
                if (execution.getAction() == EXECUTE) {
                    Lock lock = execution.getLock();
                    execution.getStages().forEach(executableStage -> runStage(lock, executableStage));
                } else {
                    keepLooping = false;
                }
            } catch (LockException exception) {
                keepLooping = false;
                eventPublisher.publish(new StageFailedEvent(exception));
                eventPublisher.publish(new PipelineFailedEvent(exception));
                if (throwExceptionIfCannotObtainLock) {
                    logger.error("Required process lock not acquired. ABORTED OPERATION", exception);
                    throw exception;

                } else {
                    logger.warn("Process lock not acquired and `throwExceptionIfCannotObtainLock == false`.\n" + "If the application should abort, make `throwExceptionIfCannotObtainLock == true`\n" + "CONTINUING THE APPLICATION WITHOUT FINISHING THE PROCESS", exception);
                }
            } catch (RuntimeException e) {
              throw e;
            } catch (Throwable throwable) {
                processAndThrow(throwable);
            }
        } while(keepLooping);
        eventPublisher.publish(new PipelineCompletedEvent());
    }


    private void runStage(Lock lock, ExecutableStage executableStage) {
        try {
            if (executableStage.doesRequireExecution()) {
                startStage(lock, executableStage);
            } else {
                skipStage(executableStage);
            }

        } catch (StageExecutionException exception) {
            logger.info("Process summary\n{}", exception.getSummary().getPretty());
            eventPublisher.publish(new StageFailedEvent(exception));
            eventPublisher.publish(new PipelineFailedEvent(exception));
            throw exception;
        } catch (Throwable generalException) {
            processAndThrow(generalException);
        }
    }

    private void startStage(Lock lock, ExecutableStage executableStage) throws StageExecutionException {
        eventPublisher.publish(new StageStartedEvent());

        logger.debug("Applied state to process:\n{}", executableStage);

        StageExecutor.Output executionOutput = stageExecutor.execute(executableStage, stageExecutionContext, lock);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publish(new StageCompletedEvent(executionOutput));
    }

    private void skipStage(ExecutableStage executableStage) {
        String stageId = "NEED_TO_ADD_STAGE_NAME";
        logger.info("Skipping stage[{}]. All the tasks are already executed.", stageId);
        eventPublisher.publish(new StageIgnoredEvent());
    }

    private void processAndThrow(Throwable generalException) throws FlamingockException {
        FlamingockException exception = generalException instanceof FlamingockException ? (FlamingockException) generalException : new FlamingockException(generalException);
        logger.error("Error executing the process. ABORTED OPERATION", exception);
        eventPublisher.publish(new StageFailedEvent(exception));
        eventPublisher.publish(new PipelineFailedEvent(exception));
        throw exception;
    }
}
