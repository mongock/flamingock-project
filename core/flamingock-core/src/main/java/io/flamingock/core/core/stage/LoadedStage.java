package io.flamingock.core.core.stage;

import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * It's the result of adding the loaded task to the ProcessDefinition
 */
public class LoadedStage {

    private final Collection<? extends TaskDescriptor> taskDescriptors;

    public LoadedStage(Collection<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    public ExecutableStage applyState(SingleAuditStageStatus state) {

        ExecutableTask.Factory factory = new ExecutableTask.Factory(state.getStatesMap());
        List<ExecutableTask> tasks = taskDescriptors
                .stream()
                .map(factory::getTasks)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ExecutableStage(tasks);
    }

}
