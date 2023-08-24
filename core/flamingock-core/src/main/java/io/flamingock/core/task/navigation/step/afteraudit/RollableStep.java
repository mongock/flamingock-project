package io.flamingock.core.task.navigation.step.afteraudit;

import io.flamingock.core.task.navigation.step.AbstractTaskStep;
import io.flamingock.core.task.navigation.step.rolledback.ManualRolledBackStep;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.executable.Rollback;
import io.flamingock.core.util.StopWatch;

public final class RollableStep extends AbstractTaskStep {
    private final Rollback rollback;


    public RollableStep(Rollback rollback) {
        super(rollback.getTask());
        this.rollback = rollback;
    }

    public ManualRolledBackStep rollback(RuntimeManager runtimeHelper) {
        StopWatch stopWatch = StopWatch.start();
        try {
            rollback.rollback(runtimeHelper);
            stopWatch.stop();
            return ManualRolledBackStep.successfulRollback(rollback, stopWatch.getDuration());
        } catch (Throwable throwable) {
            stopWatch.stop();
            return  ManualRolledBackStep.failedRollback(rollback, stopWatch.getDuration(), throwable);
        }
    }


}
