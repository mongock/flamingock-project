package io.flamingock.core.core.process.single;


import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.executable.OrderedExecutableTask;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleLoadedProcess implements LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<? extends OrderedTaskDescriptor> taskDescriptors;

    public SingleLoadedProcess(List<? extends OrderedTaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
    public SingleExecutableProcess applyState(SingleAuditProcessStatus state) {
        List<OrderedExecutableTask> tasks2 = taskDescriptors
                .stream()
                .sorted()
                .map(descriptor ->
                        OrderedExecutableTask.build(descriptor, state.getEntryStatus(descriptor.getId()).orElse(null))
                )
                .flatMap(List::stream)
                .sorted()
                .collect(Collectors.toCollection(LinkedList::new));

        return new SingleExecutableProcess(tasks2);
    }

}
