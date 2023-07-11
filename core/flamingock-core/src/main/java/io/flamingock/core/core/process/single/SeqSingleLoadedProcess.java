package io.flamingock.core.core.process.single;


import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.ExecutableTaskBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SeqSingleLoadedProcess implements LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<? extends TaskDescriptor> taskDescriptors;

    public SeqSingleLoadedProcess(List<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
    public SingleExecutableProcess applyState(SingleAuditProcessStatus state) {
        List<ExecutableTask> tasks = taskDescriptors
                .stream()
                .map(descriptor ->
                        ExecutableTaskBuilder.build(descriptor, state.getEntryStatus(descriptor.getId()).orElse(null))
                )
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return new SingleExecutableProcess(tasks);
    }

}
