package io.flamingock.core.task.executable.factory;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackBeforeExecution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.task.descriptor.SortedReflectionTaskDescriptor;
import io.flamingock.core.task.descriptor.SortedTaskDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ReflectionExecutableTask;
import io.flamingock.core.util.ReflectionUtil;
import io.flamingock.core.util.StringUtil;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Factory for ChangeUnit classes
 */
public class ChangeUnitFactory implements ExecutableTaskFactory {

    @Override
    public boolean matchesDescriptor(TaskDescriptor descriptor) {
        return descriptor instanceof SortedTaskDescriptor &&
                ((SortedReflectionTaskDescriptor) descriptor).getSource().isAnnotationPresent(ChangeUnit.class);
    }

    @Override
    public List<ReflectionExecutableTask<SortedReflectionTaskDescriptor>> extractTasks(TaskDescriptor descriptor, AuditEntryStatus initialState) {
        //It assumes "matchesDescriptor" was previously called for this descriptor.
        SortedTaskDescriptor sortedReflectionDescriptor = (SortedTaskDescriptor) descriptor;
        if (sortedReflectionDescriptor instanceof ReflectionTaskDescriptor) {
            return getTasksFromReflection(
                    (SortedReflectionTaskDescriptor) sortedReflectionDescriptor,
                    initialState);
        }

        throw new IllegalArgumentException("Unrecognized task: " + sortedReflectionDescriptor.pretty());

    }

    private List<ReflectionExecutableTask<SortedReflectionTaskDescriptor>> getTasksFromReflection(SortedReflectionTaskDescriptor taskDescriptor,
                                                                  AuditEntryStatus initialState) {

        Method executionMethod = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), Execution.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "ExecutableChangeUnit[%s] without %s method",
                        taskDescriptor.getSource().getName(),
                        Execution.class.getSimpleName())));

        Optional<Method> rollbackMethodOpt = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class);
        ReflectionExecutableTask<SortedReflectionTaskDescriptor> task = new ReflectionExecutableTask<>(
                taskDescriptor,
                AuditEntryStatus.isRequiredExecution(initialState),
                executionMethod,
                rollbackMethodOpt.orElse(null));


            /*
            If there is any rollback dependency(@BeforeExecution in ChangeUnits, legacy ChangeSet, etc.) they are added as normal task
            before the main task and, if it also provides @BeforeExecutionRollback, it's also added to the main task's rollbackChain,
            so they  are rolled back in case the main task fails.
             */
        List<ReflectionExecutableTask<SortedReflectionTaskDescriptor>> tasks = new LinkedList<>();
        getBeforeExecutionOptional(task, initialState).ifPresent(beforeExecutionTask -> {
            tasks.add(beforeExecutionTask);
            beforeExecutionTask.getRollbackChain().forEach(task::addRollback);
        });
        tasks.add(task);
        return tasks;
    }


    private Optional<ReflectionExecutableTask<SortedReflectionTaskDescriptor>> getBeforeExecutionOptional(ReflectionExecutableTask<SortedReflectionTaskDescriptor> baseTask,
                                                                          AuditEntryStatus initialState) {
        //Creates a new TaskDescriptor, based on the main one, but with the "beforeExecution id, also based on the main one"
        SortedReflectionTaskDescriptor taskDescriptor = new SortedReflectionTaskDescriptor(
                StringUtil.getBeforeExecutionId(baseTask.getDescriptor().getId()),
                baseTask.getDescriptor().getOrder(),
                baseTask.getDescriptor().getSource(),
                baseTask.getDescriptor().isRunAlways(),
                false//A beforeExecution task will never be transactional
        );

        Optional<Method> beforeExecutionMethodOptional = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), BeforeExecution.class);
        if (!beforeExecutionMethodOptional.isPresent()) {
            return Optional.empty();
        }
        Method beforeExecutionMethod = beforeExecutionMethodOptional.get();
        Optional<Method> rollbackBeforeExecution = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackBeforeExecution.class);


        return Optional.of(new ReflectionExecutableTask<>(
                taskDescriptor,
                AuditEntryStatus.isRequiredExecution(initialState),
                beforeExecutionMethod,
                rollbackBeforeExecution.orElse(null)));

    }

}