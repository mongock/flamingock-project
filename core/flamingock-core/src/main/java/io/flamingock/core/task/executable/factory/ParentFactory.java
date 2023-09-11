package io.flamingock.core.task.executable.factory;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;

import java.util.List;
import java.util.Optional;

/**
 * This is an Abstract factory of factories. Depending on the descriptor it will use one of the factories,
 * that could be ChangeUnitFactory, PluginFactory(not implemented yet), etc.
 */
public class ParentFactory implements ExecutableTaskFactory {

    public static final ParentFactory INSTANCE = new ParentFactory();

    private ParentFactory() {}

    private final ChangeUnitFactory changeUnitFactory = new ChangeUnitFactory();;


    @Override
    public boolean matchesDescriptor(TaskDescriptor descriptor) {
        return true;
    }

    @Override
    public List<? extends ExecutableTask> extractTasks(TaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        return findFactory(taskDescriptor)
                .map(factory -> factory.extractTasks(taskDescriptor, initialState))
                .orElseThrow(() -> new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName())));
    }

    private Optional<ExecutableTaskFactory> findFactory(TaskDescriptor taskDescriptor) {
        if (changeUnitFactory.matchesDescriptor(taskDescriptor)) {
            return Optional.of(changeUnitFactory);
        }
        return Optional.empty();
    }


}