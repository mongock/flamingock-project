package io.flamingock.core.driver.execution;

import io.flamingock.core.driver.lock.Lock;
import io.flamingock.core.pipeline.ExecutableStage;

import java.util.Collection;
import java.util.Collections;

public class Execution implements AutoCloseable{
    private static final Execution CONTINUE = new Execution(Action.CONTINUE, null, Collections.emptyList());

    public enum Action {
        EXECUTE, CONTINUE
    }

    public static Execution newExecution(Lock lock, Collection<ExecutableStage> stages) {
        return new Execution(Action.EXECUTE, lock, stages);
    }

    public static Execution CONTINUE() {
        return CONTINUE;
    }



    private final Action action;

    private final Lock lock;

    private final Collection<ExecutableStage> stages;

    private Execution(Action action, Lock lock, Collection<ExecutableStage> stages) {
        this.action = action;
        this.lock = lock;
        this.stages = stages;
    }

    public Action getAction() {
        return action;
    }

    public Lock getLock() {
        return lock;
    }

    public Collection<ExecutableStage> getStages() {
        return stages;
    }

    @Override
    public void close() throws Exception {
        if(lock != null) {
            lock.release();
        }
    }
}
