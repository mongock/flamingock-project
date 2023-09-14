package io.flamingock.core.task.executable.template;

import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptor;
import io.flamingock.core.task.executable.ReflectionExecutableTask;
import io.flamingock.core.task.executable.ExecutableTaskFactory;

import java.util.List;


/**
 * Factory for ChangeUnit classes
 */
public class TemplatedExecutableTaskFactory implements ExecutableTaskFactory {


    @Override
    public List<ReflectionExecutableTask<TemplatedTaskDescriptor>> extractTasks(TaskDescriptor descriptor, AuditEntryStatus initialState) {
        //It assumes "matchesDescriptor" was previously called for this descriptor.
        if (TemplatedTaskDescriptor.class.equals(descriptor.getClass())) {
            return getTasksFromReflection((TemplatedTaskDescriptor) descriptor, initialState);
        }

        throw new IllegalArgumentException(String.format("%s not able to process: %s", this.getClass().getSimpleName(), descriptor.getClass().getSimpleName()));

    }

    private List<ReflectionExecutableTask<TemplatedTaskDescriptor>> getTasksFromReflection(TemplatedTaskDescriptor taskDescriptor,
                                                                                            AuditEntryStatus initialState) {
        return null;

    }




}