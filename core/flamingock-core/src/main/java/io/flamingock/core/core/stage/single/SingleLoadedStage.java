package io.flamingock.core.core.stage.single;


import io.flamingock.core.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.core.stage.ExecutableStage;
import io.flamingock.core.core.stage.LoadedStage;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleLoadedStage implements LoadedStage<SingleAuditStageStatus, ExecutableStage> {

    private final Collection<? extends TaskDescriptor> taskDescriptors;

    public SingleLoadedStage(Collection<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
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
