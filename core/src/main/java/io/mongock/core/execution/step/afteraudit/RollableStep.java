package io.mongock.core.execution.step.afteraudit;

import io.mongock.core.execution.step.TaskStep;
import io.mongock.core.execution.step.rolledback.FailedRolledBackStep;
import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.execution.step.rolledback.SuccessRolledBackStep;
import io.mongock.core.runtime.RuntimeHelper;
import io.mongock.core.task.executable.RollableTask;
import io.mongock.core.runtime.DefaultRuntimeHelper;
import io.mongock.core.util.StopWatch;

public final  class RollableStep extends TaskStep {
    private final RollableTask rollableTask;


    RollableStep(RollableTask rollableTask) {
        super(rollableTask);
        this.rollableTask = rollableTask;
    }

    public RolledBackStep rollback(RuntimeHelper runtimeHelper) {
        StopWatch stopWatch = StopWatch.start();
        try {
            rollableTask.rollback(runtimeHelper);
            stopWatch.stop();
            return new SuccessRolledBackStep(this.getTask(), stopWatch.getDuration());
        } catch (Throwable throwable) {
            stopWatch.stop();
            return new FailedRolledBackStep(this.getTask(), stopWatch.getDuration(), throwable);
        }
    }

    @Override
    public RollableTask getTask() {
        return rollableTask;
    }

}
