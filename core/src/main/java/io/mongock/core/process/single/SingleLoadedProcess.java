package io.mongock.core.process.single;


import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.task.executable.ExecutableTask;

import java.util.LinkedList;
import java.util.List;

public class SingleLoadedProcess implements LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<? extends TaskDescriptor> taskDescriptors;

    public SingleLoadedProcess(List<? extends TaskDescriptor> taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }

    @Override
    public SingleExecutableProcess applyState(SingleAuditProcessStatus state) {
        //reused builder to avoid performing GC unnecessarily. This is always sequential
        final ExecutableTask.Builder builder = ExecutableTask.builder();
        List<ExecutableTask> tasks = new LinkedList<>();
        for(TaskDescriptor descriptor: taskDescriptors) {
            builder.clean();
            builder.setTaskDescriptor(descriptor);
            builder.setInitialState(state.getEntryStatus(descriptor.getId()).orElse(null));
            tasks.add(builder.build());
        }
        return new SingleExecutableProcess(tasks);
    }

}
