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

    /**
     * This is an Abstract factory of factories. Depending on the descriptor it will use one of the factories,
     * that could be ChangeUnitFactory, PluginFactory(not implemented yet), etc.
     */
    final class Factory implements ExecutableTaskFactory {

        private final ExecutableChangeUnitFactory changeUnitFactory;

        public Factory(Map<String, AuditEntryStatus> initialStatesMap) {
            changeUnitFactory = new ExecutableChangeUnitFactory(initialStatesMap);
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