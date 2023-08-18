package io.flamingock.core.core.execution.navigator;

import io.flamingock.core.core.audit.AuditWriter;
import io.flamingock.core.core.audit.writer.AuditItem;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.execution.navigator.tasks.beforeExecution_1.TaskWithBeforeExecution;
import io.flamingock.core.core.execution.summary.StepSummarizer;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.runtime.RuntimeManager;
import io.flamingock.core.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.descriptor.reflection.SortedReflectionTaskDescriptor;
import io.flamingock.core.core.task.executable.ExecutableTask;
import io.flamingock.core.core.util.Result;
import io.utils.TaskExecutionChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StepNavigatorTest {

    @Test
    @DisplayName("SHOULD run beforeExecution.Rollback IF task contains beforeExecution WHEN task fails")
    void test1() {
        //GIVEN
        AuditWriter auditWriterMock = mock(AuditWriter.class);
        when(auditWriterMock.writeStep(any(AuditItem.class))).thenReturn(Result.OK());

        StepSummarizer stepSummarizerMock = mock(StepSummarizer.class);
        RuntimeManager runtimeManagerMock = RuntimeManager.builder()
                .setDependencyContext(mock(DependencyInjectableContext.class))
                .setLock(mock(Lock.class))
                .build();

        //AND
        TaskDescriptor taskDescriptor = new SortedReflectionTaskDescriptor(
                "task-with-before-execution",
                "1",
                TaskWithBeforeExecution.class,
                false,
                false
        );
        List<? extends ExecutableTask> executableTasks = new ExecutableTask.Factory(new HashMap<>())
                .getTasks(taskDescriptor);

        ExecutionContext executionContext = new ExecutionContext(
                "executionId", "hsotname", "author", new HashMap<>()
        );

        StepNavigator stepNavigator = new StepNavigator(auditWriterMock, stepSummarizerMock, runtimeManagerMock, null);
        //WHEN
        try {
            stepNavigator.executeTask(executableTasks.get(0), executionContext);
            stepNavigator.executeTask(executableTasks.get(1), executionContext);
        } catch (Exception expectedException) {
            //ignore
            System.out.println(expectedException.getMessage());
        }

        //THEN
        TaskExecutionChecker checker = TaskWithBeforeExecution.checker;
        assertTrue(checker.isBeforeExecuted());
        assertTrue(checker.isExecuted());
        assertTrue(checker.isRolledBack());
        assertTrue(checker.isBeforeExecutionRolledBack());
    }

}