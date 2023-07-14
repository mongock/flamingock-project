package io.flamingock.core.core.task.executable.change;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackBeforeExecution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.SortedTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.descriptor.reflection.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.descriptor.reflection.SortedReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.task.executable.ExecutableTaskFactory;
import io.flamingock.core.core.task.executable.RollableTask;
import io.flamingock.core.core.task.executable.change.reflection.ReflectionExecutableChangeUnit;
import io.flamingock.core.core.task.executable.change.reflection.RollableReflectionChangeUnit;
import io.flamingock.core.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO JAVADOC for this
 */
public interface ExecutableChangeUnit extends ExecutableTask {

    void addRollbackDependent(RollableTask rollbackDependent);


    class Factory implements ExecutableTaskFactory {
        private final Map<String, AuditEntryStatus> statesMap;

        public Factory(Map<String, AuditEntryStatus> initialStatesMap) {
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

            ReflectionExecutableChangeUnit baseTask = new ReflectionExecutableChangeUnit(
                    taskDescriptor,
                    AuditEntryStatus.isRequiredExecution(initialState),
                    executionMethod);


            ExecutableChangeUnit decoratedBaseTaskWithRollback = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class)
                    .map(rollbackMethod ->
                            (ExecutableChangeUnit) new RollableReflectionChangeUnit(baseTask, rollbackMethod)
                    ).orElse(baseTask);

            /*
            If there is any rollback dependency(@BeforeExecution in ChangeUnits) they are added as normal task
            before the main task and, if it also provides BeforeExecutionRollback, also added to the main task as rollback
            dependent, so they  are rolled back in case the main task fails.
             */
            List<ExecutableTask> tasks = new LinkedList<>();
            getBeforeExecutionOptional(baseTask)
                    .ifPresent(beforeExecutionTask -> {
                        tasks.add(beforeExecutionTask);
                        if (beforeExecutionTask instanceof RollableTask) {
                            decoratedBaseTaskWithRollback.addRollbackDependent((RollableTask) beforeExecutionTask);
                        }
                    });
            tasks.add(decoratedBaseTaskWithRollback);
            return tasks;
        }


        private Optional<ExecutableTask> getBeforeExecutionOptional(ReflectionExecutableChangeUnit baseTask) {
            //Creates a new TaskDescriptor, based on the main one, but with the "beforeExecution id, also based on the main one"
            SortedReflectionTaskDescriptor taskDescriptor = new SortedReflectionTaskDescriptor(
                    BeforeExecutionIdGenerator.getId(baseTask.getDescriptor().getId()),
                    baseTask.getDescriptor().getOrder(),
                    baseTask.getDescriptor().getSource(),
                    baseTask.getDescriptor().isRunAlways()
            );

            Optional<Method> beforeExecutionMethodOptional = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), BeforeExecution.class);
            if (!beforeExecutionMethodOptional.isPresent()) {
                return Optional.empty();
            }
            AuditEntryStatus initialState = statesMap.get(taskDescriptor.getId());
            Method beforeExecutionMethod = beforeExecutionMethodOptional.get();
            ReflectionExecutableChangeUnit beforeExecutionTask = new ReflectionExecutableChangeUnit(
                    taskDescriptor,
                    AuditEntryStatus.isRequiredExecution(initialState),
                    beforeExecutionMethod);

            ExecutableChangeUnit decoratedBeforeExecutionTaskWithRollback = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackBeforeExecution.class)
                    .map(rollbackMethod ->
                            (ExecutableChangeUnit) new RollableReflectionChangeUnit(beforeExecutionTask, rollbackMethod)
                    ).orElse(baseTask);
            return Optional.of(decoratedBeforeExecutionTaskWithRollback);

        }

    }
}
