package io.flamingock.core.task.executable;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.task.Task;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.change.ExecutableChangeUnitFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    void addRollback(Rollback rollback);

    List<? extends Rollback> getRollbackChain();


}