package io.flamingock.core.task.navigation.navigator;

import io.flamingock.core.audit.AuditWriter;
import io.flamingock.core.audit.writer.AuditItem;
import io.flamingock.core.pipeline.execution.StageExecutionContext;
import io.flamingock.core.task.executable.factory.ParentFactory;
import io.flamingock.core.task.navigation.navigator.beforeExecution_1.TaskWithBeforeExecution;
import io.flamingock.core.task.navigation.summary.StepSummarizer;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.util.Result;
import io.utils.EmptyTransactionWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static io.utils.TestTaskExecution.BEFORE_EXECUTION;
import static io.utils.TestTaskExecution.EXECUTION;
import static io.utils.TestTaskExecution.ROLLBACK_BEFORE_EXECUTION;
import static io.utils.TestTaskExecution.ROLLBACK_EXECUTION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StepNavigatorTest {

    public static final StageExecutionContext EXECUTION_CONTEXT = new StageExecutionContext(
            "executionId", "hsotname", "author", new HashMap<>()
    );

    @BeforeEach
    void beforeEach() {
        TaskWithBeforeExecution.checker.reset();
    }




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
        TaskDescriptor taskDescriptor = new ReflectionTaskDescriptor(
                "task-with-before-execution",
                "1",
                TaskWithBeforeExecution.class,
                false,
                false
        );
        List<? extends ExecutableTask> executableTasks = ParentFactory.INSTANCE
                .extractTasks(taskDescriptor, null);

        StageExecutionContext stageExecutionContext = new StageExecutionContext(
                "executionId", "hsotname", "author", new HashMap<>()
        );

        StepNavigator stepNavigator = new StepNavigator(auditWriterMock, stepSummarizerMock, runtimeManagerMock, null);
        //WHEN

        stepNavigator.executeTask(executableTasks.get(0), stageExecutionContext);
        stepNavigator.executeTask(executableTasks.get(1), stageExecutionContext);

        //THEN
        TaskWithBeforeExecution.checker.checkOrderStrict(
                BEFORE_EXECUTION,
                EXECUTION,
                ROLLBACK_EXECUTION,
                ROLLBACK_BEFORE_EXECUTION
        );
    }


    @Test
    @DisplayName("SHOULD run beforeExecution.Rollback " +
            "AND not execution.rollback " +
            "IF provided transactionWrapper " +
            "WHEN task fails")
    void test2() {
        //GIVEN
        AuditWriter auditWriterMock = mock(AuditWriter.class);
        when(auditWriterMock.writeStep(any(AuditItem.class))).thenReturn(Result.OK());

        StepSummarizer stepSummarizerMock = mock(StepSummarizer.class);
        RuntimeManager runtimeManagerMock = RuntimeManager.builder()
                .setDependencyContext(mock(DependencyInjectableContext.class))
                .setLock(mock(Lock.class))
                .build();

        //AND
        TaskDescriptor taskDescriptor = new ReflectionTaskDescriptor(
                "task-with-before-execution",
                "1",
                TaskWithBeforeExecution.class,
                false,
                true
        );
        List<? extends ExecutableTask> executableTasks = ParentFactory.INSTANCE
                .extractTasks(taskDescriptor, null);

        EmptyTransactionWrapper transactionWrapper = new EmptyTransactionWrapper();
        StepNavigator stepNavigator = new StepNavigator(auditWriterMock, stepSummarizerMock, runtimeManagerMock, transactionWrapper);
        //WHEN
        stepNavigator.executeTask(executableTasks.get(0), EXECUTION_CONTEXT);
        stepNavigator.executeTask(executableTasks.get(1), EXECUTION_CONTEXT);

        //THEN
        TaskWithBeforeExecution.checker.checkOrderStrict(
                BEFORE_EXECUTION,
                EXECUTION,
                ROLLBACK_BEFORE_EXECUTION
        );

        assertTrue(transactionWrapper.isCalled());
    }

}