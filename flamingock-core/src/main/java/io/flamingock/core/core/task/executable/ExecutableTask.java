package io.flamingock.core.core.task.executable;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.task.Task;
import io.flamingock.core.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.executable.change.ReflectionExecutableChangeUnit;
import io.flamingock.core.core.task.executable.change.RollableReflectionChangeUnit;
import io.flamingock.core.core.util.ReflectionUtil;

import java.lang.reflect.Method;

public interface ExecutableTask extends Task {

    void execute(RuntimeManager runtimeHelper);

    String getExecutionMethodName();

    boolean isInitialExecutionRequired();

    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private TaskDescriptor taskDescriptor;
        private AuditEntryStatus initialState;

        private Builder() {
        }

        public Builder clean() {
            taskDescriptor = null;
            initialState = null;
            return this;
        }

        public Builder setTaskDescriptor(TaskDescriptor taskDescriptor) {
            this.taskDescriptor = taskDescriptor;
            return this;
        }

        public Builder setInitialState(AuditEntryStatus initialState) {
            this.initialState = initialState;
            return this;
        }

        public ExecutableTask build() {
            if (taskDescriptor instanceof ReflectionTaskDescriptor) {
                return getTaskFromReflection((ReflectionTaskDescriptor) taskDescriptor, initialState);
            } else {
                throw new IllegalArgumentException(String.format("ExecutableTask type not recognised[%s]", taskDescriptor.getClass().getName()));
            }
        }

        private static ExecutableTask getTaskFromReflection(ReflectionTaskDescriptor taskDescriptor, AuditEntryStatus initialState) {
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

            return ReflectionUtil.findFirstMethodAnnotated(taskDescriptor.getSource(), RollbackExecution.class)
                    .map(rollbackMethod ->
                            (ExecutableTask) new RollableReflectionChangeUnit(reflectionChangeUnit, rollbackMethod)
                    ).orElse(reflectionChangeUnit);
        }
    }
}