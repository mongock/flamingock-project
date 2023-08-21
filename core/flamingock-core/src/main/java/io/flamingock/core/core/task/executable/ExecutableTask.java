package io.flamingock.core.core.task.executable;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnit;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    void addDependentRollbacks(Rollback rollbackDependent);

    //TODO remove getRollback() method and add it to `addDependentRollbacks`, but change it to simply `addRollback`
    Optional<Rollback> getRollback();

    List<? extends Rollback> getDependentTasks();

    /**
     * This is an Abstract factory of factories. Depending on the descriptor it will use one of the factories,
     * that could be ChangeUnitFactory, PluginFactory(not implemented yet), etc.
     */
    final class Factory implements ExecutableTaskFactory {

        private final ExecutableChangeUnit.Factory changeUnitFactory;

        public Factory(Map<String, AuditEntryStatus> initialStatesMap) {
            changeUnitFactory = new ExecutableChangeUnit.Factory(initialStatesMap);
        }

        @Override
        public boolean matchesDescriptor(TaskDescriptor descriptor) {
            return true;
        }

        @Override
        public List<? extends ExecutableTask> getTasks(TaskDescriptor taskDescriptor) {
            return findFactory(taskDescriptor)
                    .map(factory -> factory.getTasks(taskDescriptor))
                    .orElseThrow(() -> new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName())));
        }

        private Optional<ExecutableTaskFactory> findFactory(TaskDescriptor taskDescriptor) {
            if (changeUnitFactory.matchesDescriptor(taskDescriptor)) {
                return Optional.of(changeUnitFactory);
            }
            return Optional.empty();
        }


    }

}