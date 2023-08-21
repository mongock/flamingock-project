package io.flamingock.core.core.execution.step.afteraudit;

import io.flamingock.core.core.execution.step.AbstractTaskStep;
import io.flamingock.core.core.execution.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.executable.RollableTask;
import io.flamingock.core.core.util.StopWatch;

public final class RollableStep extends AbstractTaskStep {
    private final RollableTask rollableTask;


    public RollableStep(RollableTask rollableTask) {
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
