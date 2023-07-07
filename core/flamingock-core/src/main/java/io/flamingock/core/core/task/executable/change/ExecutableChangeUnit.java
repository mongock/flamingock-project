package io.flamingock.core.core.task.executable.change;

import io.flamingock.core.core.task.executable.OrderedExecutableTask;
import io.flamingock.core.core.task.executable.RollableTask;

/**
 * TODO JAVADOC for this
 */
public interface ExecutableChangeUnit extends OrderedExecutableTask {

    void addRollbackDependent(RollableTask rollbackDependent);
}
