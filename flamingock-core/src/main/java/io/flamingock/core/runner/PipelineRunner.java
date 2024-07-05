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
import io.flamingock.core.pipeline.execution.ExecutionContext;
import io.flamingock.core.pipeline.execution.OrphanExecutionContext;
import io.flamingock.core.pipeline.execution.StageExecutionException;
import io.flamingock.core.pipeline.execution.StageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineRunner implements Runner {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunner.class);

    private final RunnerId runnerId;

    private final Pipeline pipeline;

    private final ExecutionPlanner executionPlanner;

    private final EventPublisher eventPublisher;

    private final boolean throwExceptionIfCannotObtainLock;

    private final StageExecutor stageExecutor;

    private final OrphanExecutionContext orphanExecutionContext;

    private final Runnable finalizer;

    public PipelineRunner(RunnerId runnerId,
                          Pipeline pipeline,
                          ExecutionPlanner executionPlanner,
                          StageExecutor stageExecutor,
                          OrphanExecutionContext orphanExecutionContext,
                          EventPublisher eventPublisher,
                          boolean throwExceptionIfCannotObtainLock,
                          Runnable finalizer) {
        this.runnerId = runnerId;
        this.pipeline = pipeline;
        this.executionPlanner = executionPlanner;
        this.stageExecutor = stageExecutor;
        this.orphanExecutionContext = orphanExecutionContext;
        this.eventPublisher = eventPublisher;
        this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
        this.finalizer = finalizer;
    }

    private void run(Pipeline pipeline) throws FlamingockException {
        eventPublisher.publish(new PipelineStartedEvent());
        boolean keepLooping = true;
        do {
            try {
                keepLooping = executionPlanner.executeIfRequired(pipeline, this::runStage);

            } catch (LockException exception) {
                keepLooping = false;
                eventPublisher.publish(new StageFailedEvent(exception));
                eventPublisher.publish(new PipelineFailedEvent(exception));
                if (throwExceptionIfCannotObtainLock) {
                    logger.error("Required process lock not acquired. ABORTED OPERATION", exception);
                    throw exception;

                } else {
                    logger.warn("Process lock not acquired and `throwExceptionIfCannotObtainLock == false`.\n" +
                            "If the application should abort, make `throwExceptionIfCannotObtainLock == true`\n" +
                            "CONTINUING THE APPLICATION WITHOUT FINISHING THE PROCESS", exception);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                processAndThrow(throwable);
            }
        } while (keepLooping);
        eventPublisher.publish(new PipelineCompletedEvent());
    }

    private void runStage(String executionId, Lock lock, ExecutableStage executableStage) {
        try {
            if (executableStage.doesRequireExecution()) {
                startStage(executionId, lock, executableStage);
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

    private void startStage(String executionId, Lock lock, ExecutableStage executableStage) throws StageExecutionException {
        eventPublisher.publish(new StageStartedEvent());

        logger.debug("Applied state to process:\n{}", executableStage);

        ExecutionContext executionContext = new ExecutionContext(
                executionId,
                orphanExecutionContext.getHostname(),
                orphanExecutionContext.getAuthor(),
                orphanExecutionContext.getMetadata());
        StageExecutor.Output executionOutput = stageExecutor.executeStage(executableStage, executionContext, lock);
        logger.info("Finished process successfully\nProcess summary\n{}", executionOutput.getSummary().getPretty());
        eventPublisher.publish(new StageCompletedEvent(executionOutput));
    }

    private void skipStage(ExecutableStage executableStage) {
        logger.info("Skipping stage[{}]. All the tasks are already executed.", executableStage.getName());
        eventPublisher.publish(new StageIgnoredEvent());
    }

    private void processAndThrow(Throwable generalException) throws FlamingockException {
        FlamingockException exception = generalException instanceof FlamingockException ? (FlamingockException) generalException : new FlamingockException(generalException);
        logger.error("Error executing the process. ABORTED OPERATION", exception);
        eventPublisher.publish(new StageFailedEvent(exception));
        eventPublisher.publish(new PipelineFailedEvent(exception));
        throw exception;
    }

    @Override
    public void run() {
        try {
            this.run(pipeline);
        } finally {
            finalizer.run();
        }
    }
}
