package io.flamingock.core.task.executable;

import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.Task;

import java.util.List;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    void addRollback(Rollback rollback);

    List<? extends Rollback> getRollbackChain();


}