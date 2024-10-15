package io.flamingock.core.legacy;

import io.flamingock.commons.utils.Result;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.domain.AuditItem;
import io.flamingock.core.engine.lock.Lock;
import io.flamingock.core.legacy_old.utils.TaskExecutionChecker;
import io.flamingock.core.legacy_old.utils.TestTaskExecution;
import io.flamingock.core.pipeline.execution.ExecutionContext;
import io.flamingock.core.pipeline.execution.TaskSummarizer;
import io.flamingock.core.runtime.RuntimeManager;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.task.descriptor.ChangeUnitTaskDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.executable.ParentExecutableTaskFactory;
import io.flamingock.core.task.navigation.navigator.StepNavigator;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LegacyTestRunner {

    static void runTest(Class<?> changeUnitClass,
                        int expectedNumberOfExecutableTasks,
                        TaskExecutionChecker checker,
                        TestTaskExecution... executionSteps
    ) {
        checker.reset();
        AuditWriter auditWriterMock = mock(AuditWriter.class);
        when(auditWriterMock.writeStep(any(AuditItem.class))).thenReturn(Result.OK());

        TaskSummarizer stepSummarizerMock = new TaskSummarizer("taskId");
        RuntimeManager runtimeManagerMock = RuntimeManager.builder()
                .setDependencyContext(mock(DependencyInjectableContext.class))
                .setLock(mock(Lock.class))
                .build();

        //AND
        TaskDescriptor taskDescriptor = new ChangeUnitTaskDescriptor(
                "taskId",
                "1",
                changeUnitClass,
                false,
                false,
                false
        );
        List<? extends ExecutableTask> executableTasks = ParentExecutableTaskFactory.INSTANCE
                .extractTasks("stage_name", taskDescriptor, null);

        ExecutionContext stageExecutionContext = new ExecutionContext(
                "executionId", "hostname", "author", new HashMap<>()
        );

        StepNavigator stepNavigator = new StepNavigator(auditWriterMock, stepSummarizerMock, runtimeManagerMock, null);

        executableTasks.forEach(executableTask -> stepNavigator.executeTask(executableTask, stageExecutionContext));

        Assertions.assertEquals(expectedNumberOfExecutableTasks, executableTasks.size());
        checker.checkOrderStrict(Arrays.asList(executionSteps));
    }
}
