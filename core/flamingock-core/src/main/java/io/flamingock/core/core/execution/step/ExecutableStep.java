package io.flamingock.core.core.execution.step;

import io.flamingock.core.core.execution.step.execution.ExecutionStep;
import io.flamingock.core.core.execution.step.execution.FailedExecutionStep;
import io.flamingock.core.core.execution.step.execution.SuccessExecutionStep;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.StopWatch;

public class ExecutableStep extends TaskStep {

    public ExecutableStep(ExecutableTask task) {
        super(task);
    }

    public ExecutionStep execute(RuntimeManager runtimeHelper) {
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