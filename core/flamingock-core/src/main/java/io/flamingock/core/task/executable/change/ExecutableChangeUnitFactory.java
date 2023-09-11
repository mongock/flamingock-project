package io.flamingock.core.task.executable.change;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackBeforeExecution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.task.descriptor.SortedTaskDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.reflection.ReflectionTaskDescriptor;
import io.flamingock.core.task.descriptor.reflection.SortedReflectionTaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.executable.ExecutableTaskFactory;
import io.flamingock.core.task.executable.change.reflection.ReflectionExecutableChangeUnit;
import io.flamingock.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExecutableChangeUnitFactory  implements ExecutableTaskFactory {
    private final Map<String, AuditEntryStatus> statesMap;

    public ExecutableChangeUnitFactory(Map<String, AuditEntryStatus> initialStatesMap) {
        this.statesMap = initialStatesMap;
    }

    @Override
    public boolean matchesDescriptor(TaskDescriptor descriptor) {
        return descriptor instanceof SortedTaskDescriptor &&
                ((SortedReflectionTaskDescriptor) descriptor).getSource().isAnnotationPresent(ChangeUnit.class);
    }

    @Override
    public List<? extends ExecutableTask> getTasks(TaskDescriptor descriptor) {
        //It assumes "matchesDescriptor" was previously called for this descriptor.
        SortedTaskDescriptor sortedReflectionDescriptor = (SortedTaskDescriptor) descriptor;
        if (sortedReflectionDescriptor instanceof ReflectionTaskDescriptor) {
            return getTasksFromReflection((SortedReflectionTaskDescriptor) sortedReflectionDescriptor);
        }

        throw new IllegalArgumentException("Unrecognized task: " + sortedReflectionDescriptor.pretty());

    }

    private List<? extends ExecutableTask> getTasksFromReflection(SortedReflectionTaskDescriptor taskDescriptor) {

        AuditEntryStatus initialState = statesMap.get(taskDescriptor.getId());

        Method executionMethod = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), Execution.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "ExecutableChangeUnit[%s] without %s method",
                        taskDescriptor.getSource().getName(),
                        Execution.class.getSimpleName())));

        Optional<Method> rollbackMethodOpt = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class);
        ReflectionExecutableChangeUnit task = new ReflectionExecutableChangeUnit(
                taskDescriptor,
                AuditEntryStatus.isRequiredExecution(initialState),
                executionMethod,
                rollbackMethodOpt.orElse(null));


            /*
            If there is any rollback dependency(@BeforeExecution in ChangeUnits, legacy ChangeSet, etc.) they are added as normal task
            before the main task and, if it also provides @BeforeExecutionRollback, it's also added to the main task's rollbackChain,
            so they  are rolled back in case the main task fails.
             */
        List<ExecutableTask> tasks = new LinkedList<>();
        getBeforeExecutionOptional(task).ifPresent(beforeExecutionTask -> {
            tasks.add(beforeExecutionTask);
            beforeExecutionTask.getRollbackChain().forEach(task::addRollback);
        });
        tasks.add(task);
        return tasks;
    }


    private Optional<ExecutableTask> getBeforeExecutionOptional(ReflectionExecutableChangeUnit baseTask) {
        //Creates a new TaskDescriptor, based on the main one, but with the "beforeExecution id, also based on the main one"
        SortedReflectionTaskDescriptor taskDescriptor = new SortedReflectionTaskDescriptor(
                BeforeExecutionIdGenerator.getId(baseTask.getDescriptor().getId()),
                baseTask.getDescriptor().getOrder(),
                baseTask.getDescriptor().getSource(),
                baseTask.getDescriptor().isRunAlways(),
                false//A beforeExecution task will never be transactional
        );

        Optional<Method> beforeExecutionMethodOptional = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), BeforeExecution.class);
        if (!beforeExecutionMethodOptional.isPresent()) {
            return Optional.empty();
        }
        AuditEntryStatus initialState = statesMap.get(taskDescriptor.getId());
        Method beforeExecutionMethod = beforeExecutionMethodOptional.get();
        Optional<Method> rollbackBeforeExecution = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackBeforeExecution.class);


        return Optional.of(new ReflectionExecutableChangeUnit(
                taskDescriptor,
                AuditEntryStatus.isRequiredExecution(initialState),
                beforeExecutionMethod,
                rollbackBeforeExecution.orElse(null)));

    }

}