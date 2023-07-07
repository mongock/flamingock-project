package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.change.ReflectionExecutableChangeUnit;
import io.flamingock.core.core.task.executable.change.RollableReflectionChangeUnit;
import io.flamingock.core.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExecutableTaskBuilder {

    private TaskDescriptor taskDescriptor;
    private AuditEntryStatus initialState;

    ExecutableTaskBuilder() {
    }

    public ExecutableTaskBuilder clean() {
        taskDescriptor = null;
        initialState = null;
        return this;
    }

    public ExecutableTaskBuilder setTaskDescriptor(TaskDescriptor taskDescriptor) {
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

        ExecutableTask mainTask = ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class)
                .map(rollbackMethod ->
                        (ExecutableTask) new RollableReflectionChangeUnit(reflectionChangeUnit, rollbackMethod)
                ).orElse(reflectionChangeUnit);

        return Collections.singletonList(mainTask);
    }
}
