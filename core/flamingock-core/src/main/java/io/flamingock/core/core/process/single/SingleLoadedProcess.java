package io.flamingock.core.core.process.single;


import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleLoadedProcess implements LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final Collection<? extends TaskDescriptor> taskDescriptors;

    public SingleLoadedProcess(Collection<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
    public SingleExecutableProcess applyState(SingleAuditProcessStatus state) {

        ExecutableTask.Factory factory = new ExecutableTask.Factory(state.getStatesMap());
        List<ExecutableTask> tasks = taskDescriptors
                .stream()
                .map(factory::getTasks)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return new SingleExecutableProcess(tasks);
    }

}
