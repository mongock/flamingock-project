package io.mongock.core.task.executable;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.core.audit.domain.AuditEntryStatus;
import io.mongock.core.task.Task;
import io.mongock.core.task.descriptor.ReflectionTaskDescriptor;
import io.mongock.core.task.descriptor.TaskDescriptor;
import io.mongock.core.task.executable.change.ReflectionExecutableChangeUnit;
import io.mongock.core.task.executable.change.RollableReflectionChangeUnit;
import io.mongock.core.util.ReflectionUtil;
import io.mongock.core.util.RuntimeHelper;

import java.lang.reflect.Method;

public interface ExecutableTask extends Task {

    void execute(RuntimeHelper runtimeHelper);

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