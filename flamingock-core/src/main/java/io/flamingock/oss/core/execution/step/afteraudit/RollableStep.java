package io.flamingock.oss.core.execution.step.afteraudit;

import io.flamingock.oss.core.runtime.RuntimeManager;
import io.flamingock.oss.core.task.executable.RollableTask;
import io.flamingock.oss.core.util.StopWatch;
import io.flamingock.oss.core.execution.step.TaskStep;
import io.flamingock.oss.core.execution.step.rolledback.ManualRolledBackStep;

public final class RollableStep extends TaskStep {
    private final RollableTask rollableTask;


    RollableStep(RollableTask rollableTask) {
        super(rollableTask);
        this.rollableTask = rollableTask;
    }

    public ManualRolledBackStep rollback(RuntimeManager runtimeHelper) {
        StopWatch stopWatch = StopWatch.start();
        try {
            rollableTask.rollback(runtimeHelper);
            stopWatch.stop();
            return ManualRolledBackStep.successfulRollback(this.getTask(), stopWatch.getDuration());
        } catch (Throwable throwable) {
            stopWatch.stop();
            return  ManualRolledBackStep.failedRollback(this.getTask(), stopWatch.getDuration(), throwable);
        }
    }

    @Override
    public RollableTask getTask() {
        return rollableTask;
    }

}
