package io.mongock.core.execution.step;

import io.mongock.core.execution.step.execution.ExecutionStep;
import io.mongock.core.execution.step.execution.FailedExecutionStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.ExecutableTask;
import io.mongock.core.runtime.DefaultRuntimeHelper;
import io.mongock.core.util.StopWatch;

public class ExecutableStep extends TaskStep {

    public ExecutableStep(ExecutableTask task) {
        super(task);
    }

    public ExecutionStep execute(RuntimeHelper runtimeHelper) {
        StopWatch stopWatch = StopWatch.start();
        try {
            task.execute(runtimeHelper);
            return SuccessExecutionStep.instance(this, stopWatch.stop());
        } catch (Throwable throwable) {
            stopWatch.stop();
            return FailedExecutionStep.instance(this, stopWatch.getDuration(), throwable);
        }

    }

}
