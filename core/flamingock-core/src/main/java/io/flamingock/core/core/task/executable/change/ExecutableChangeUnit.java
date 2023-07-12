package io.flamingock.core.core.task.executable.change;

import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.RollableTask;

/**
 * TODO JAVADOC for this
 */
public interface ExecutableChangeUnit extends ExecutableTask {

    void addRollbackDependent(RollableTask rollbackDependent);
}
