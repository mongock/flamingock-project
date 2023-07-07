package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.BeforeExecution;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.impl.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
import io.flamingock.core.core.task.executable.change.ExecutableChangeUnit;
import io.flamingock.core.core.task.executable.change.ReflectionExecutableChangeUnit;
import io.flamingock.core.core.task.executable.change.RollableReflectionChangeUnit;
import io.flamingock.core.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExecutableTaskBuilder {

    private OrderedTaskDescriptor taskDescriptor;
    private AuditEntryStatus initialState;

    ExecutableTaskBuilder() {
    }

    public ExecutableTaskBuilder clean() {
        taskDescriptor = null;
        initialState = null;
        return this;
    }

    public ExecutableTaskBuilder setTaskDescriptor(OrderedTaskDescriptor taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
        return this;
    }

    public ExecutableTaskBuilder setInitialState(AuditEntryStatus initialState) {
        this.initialState = initialState;
        return this;
    }

    public List<ExecutableTask> build() {
        if (taskDescriptor instanceof ReflectionTaskDescriptor) {
            return getTaskFromReflection((ReflectionTaskDescriptor) taskDescriptor, initialState);
        } else {
            throw new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
        }
    }

    private static List<ExecutableTask> getTaskFromReflection(ReflectionTaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
        if (!taskDescriptor.getSource().isAnnotationPresent(ChangeUnit.class)) {
            throw new IllegalArgumentException(String.format(
                    "ExecutableChangeUnit source class[%s] must be annotated with %s",
                    taskDescriptor.getSource().getName(),
                    ChangeUnit.class.getName()));
        }

        Method executionMethod = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), Execution.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "ExecutableChangeUnit[%s] without %s method",
                        taskDescriptor.getSource().getName(),
                        Execution.class.getSimpleName())));

        ChangeUnit changeUnitAnnotation = taskDescriptor.getSource().getAnnotation(ChangeUnit.class);
        ReflectionExecutableChangeUnit reflectionChangeUnit = new ReflectionExecutableChangeUnit(
                taskDescriptor,
                changeUnitAnnotation.order(),
                AuditEntryStatus.isRequiredExecution(initialState),
                executionMethod);

        ExecutableChangeUnit mainTask = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class)
                .map(rollbackMethod ->
                        (ExecutableChangeUnit) new RollableReflectionChangeUnit(reflectionChangeUnit, rollbackMethod)
                ).orElse(reflectionChangeUnit);

        /*
        If there is any rollback dependency(@BeforeExecution in ChangeUnits) they are added as normal task
        before the main task and also added to the main task as rollback dependent, so they  are rolled back in case
        the main task fails.
         */
        List<ExecutableTask> tasks = new LinkedList<>();
        getRollbackDependentOptional(taskDescriptor).ifPresent(rollbackDependent -> {
            tasks.add(rollbackDependent);
            mainTask.addRollbackDependent(rollbackDependent);
        });
        tasks.add(mainTask);
        return tasks;
    }

    private static Optional<RollableTask> getRollbackDependentOptional(ReflectionTaskDescriptor taskDescriptor) {
//        Optional<Method> beforeExecutionMethodOptional = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), BeforeExecution.class);
//        if(!beforeExecutionMethodOptional.isPresent()) {
//            return Optional.empty();
//        }
//        Method beforeExecutionMethod = beforeExecutionMethodOptional.get();
return null;
//        return ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), BeforeExecution.class).map(beforeExecutionMethod -> {
//            ExecutableChangeUnit mainTask = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class)
//                    .map(rollbackMethod ->
//                            (ExecutableChangeUnit) new RollableReflectionChangeUnit(reflectionChangeUnit, rollbackMethod)
//                    ).orElse(reflectionChangeUnit);
//        });
    }
}
