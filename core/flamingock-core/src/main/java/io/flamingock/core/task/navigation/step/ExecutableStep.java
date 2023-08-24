package io.flamingock.core.task.navigation.step;

import io.flamingock.core.task.navigation.step.execution.ExecutionStep;
import io.flamingock.core.task.navigation.step.execution.FailedExecutionStep;
import io.flamingock.core.task.navigation.step.execution.SuccessExecutionStep;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.StopWatch;

public class ExecutableStep extends AbstractTaskStep {

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
