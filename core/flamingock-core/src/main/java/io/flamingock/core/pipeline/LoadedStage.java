package io.flamingock.core.pipeline;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.audit.domain.AuditStageStatus;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.executable.ParentExecutableTaskFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * It's the result of adding the loaded task to the ProcessDefinition
 */
public class LoadedStage {

    private final Collection<? extends TaskDescriptor> taskDescriptors;
    private final boolean parallel;

    private final ParentExecutableTaskFactory factory;

    public LoadedStage(Collection<? extends TaskDescriptor> taskDescriptors, boolean parallel) {
        this.taskDescriptors = taskDescriptors;
        this.parallel = parallel;
        factory = ParentExecutableTaskFactory.INSTANCE;
    }

    public ExecutableStage applyState(AuditStageStatus state) {

        Map<String, AuditEntryStatus> statesMap = state.getEntryStatesMap();

        List<ExecutableTask> tasks = taskDescriptors
                .stream()
                .map(taskDescriptor -> factory.extractTasks(taskDescriptor, statesMap.get(taskDescriptor.getId())))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ExecutableStage(tasks, parallel);
    }

}
