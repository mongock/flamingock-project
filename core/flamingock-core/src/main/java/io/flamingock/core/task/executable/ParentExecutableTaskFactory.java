package io.flamingock.core.task.executable;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptor;
import io.flamingock.core.task.executable.template.TemplatedExecutableTaskFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is an Abstract factory of factories. Depending on the descriptor it will use one of the factories,
 * that could be ChangeUnitFactory, PluginFactory(not implemented yet), etc.
 */
public class ParentExecutableTaskFactory implements ExecutableTaskFactory {

    public static final ParentExecutableTaskFactory INSTANCE = new ParentExecutableTaskFactory();

    private static final Map<Class<? extends TaskDescriptor>, ExecutableTaskFactory> factories;

    static {
        factories = new HashMap<>();
        factories.put(ReflectionTaskDescriptor.class, new ChangeUnitFactory());
        factories.put(TemplatedTaskDescriptor.class, new TemplatedExecutableTaskFactory());
    }

    private ParentExecutableTaskFactory() {
    }


    @Override
    public List<? extends ExecutableTask> extractTasks(TaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        return findFactory(taskDescriptor)
                .map(executableTaskFactory -> executableTaskFactory.extractTasks(taskDescriptor, initialState))
                .orElseThrow(() -> new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName())));
    }

    private static Optional<ExecutableTaskFactory> findFactory(TaskDescriptor taskDescriptor) {
        return Optional.ofNullable(factories.get(taskDescriptor.getClass()));
    }


}