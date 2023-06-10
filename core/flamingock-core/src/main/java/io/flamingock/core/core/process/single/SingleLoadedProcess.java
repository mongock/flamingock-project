package io.flamingock.core.core.process.single;


import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleLoadedProcess implements LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<? extends TaskDescriptor> taskDescriptors;

    public SingleLoadedProcess(List<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
    public SingleExecutableProcess applyState(SingleAuditProcessStatus state) {
        //reused builder to avoid performing GC unnecessarily. This is always sequential
        final ExecutableTask.Builder builder = ExecutableTask.builder();
        List<ExecutableTask> tasks2 = taskDescriptors
                .stream()
                .sorted()
                .map(descriptor ->
                        builder.clean()
                                .setTaskDescriptor(descriptor)
                                .setInitialState(state.getEntryStatus(descriptor.getId()).orElse(null))
                                .build()
                ).sorted()
                .collect(Collectors.toCollection(LinkedList::new));

        return new SingleExecutableProcess(tasks2);
    }

}
