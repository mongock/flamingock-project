package io.mongock.core.process;

import io.mongock.core.audit.domain.AuditResult;
import io.mongock.core.audit.writer.AuditItem;
import io.mongock.core.audit.writer.AuditWriter;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.execution.executor.SingleProcessExecutor;
import io.mongock.core.execution.summary.StepSummaryLine;
import io.mongock.core.process.single.SingleExecutableProcess;
import io.mongock.core.process.stubs.task.FailedTestExecutableTask;
import io.mongock.core.process.stubs.task.SuccessTestExecutableTask;
import io.mongock.core.runtime.DefaultRuntimeHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SingleRunnableProcessTest {

    @Test
    @DisplayName("GIVEN a non-previously-executed list of tasks\n" +
            "WHEN the processor is executed\n" +
            "THEN it should run all the tasks successfully")
    void shouldRunAllTasks() {
        //SETUP
        AuditWriter<?> stateSaver = mock(AuditWriter.class);
        when(stateSaver.writeStep(any(AuditItem.class))).thenReturn(new AuditResult.Ok());

        //GIVEN
        SuccessTestExecutableTask task1 = new SuccessTestExecutableTask("task1");
        SuccessTestExecutableTask task2 = new SuccessTestExecutableTask("task2");
        SuccessTestExecutableTask task3 = new SuccessTestExecutableTask("task3");
        SingleExecutableProcess process = new SingleExecutableProcess(Arrays.asList(task1, task2, task3));

        //WHEN
        ExecutionContext executionContext = new ExecutionContext(null, null, null, null);
        List<StepSummaryLine> steps = new SingleProcessExecutor(stateSaver)
                .run(process, executionContext, mock(DefaultRuntimeHelper.class))
                .getSummary()
                .getLines();

        //THEN
        //Task1's summary lines
        assertEquals(steps.get(0), new StepSummaryLine.InitialSummaryLine(task1.getDescriptor()));
        assertEquals(steps.get(1).getId(), task1.getDescriptor().getId());
        assertEquals(((StepSummaryLine.ExecutedSummaryLine) steps.get(1)).getPrettyResult(), true);
        assertEquals(steps.get(2).getId(), task1.getDescriptor().getId());
        assertEquals(((StepSummaryLine.AfterExecutionAuditSummaryLine) steps.get(2)).getPrettyResult(), true);
        //Task2's summary lines
        assertEquals(steps.get(3), new StepSummaryLine.InitialSummaryLine(task2.getDescriptor()));
        assertEquals(steps.get(4).getId(), task2.getDescriptor().getId());
        assertEquals(((StepSummaryLine.ExecutedSummaryLine) steps.get(4)).getPrettyResult(), true);
        assertEquals(steps.get(5).getId(), task2.getDescriptor().getId());
        assertEquals(((StepSummaryLine.AfterExecutionAuditSummaryLine) steps.get(5)).getPrettyResult(), true);
        //Task2's summary lines
        assertEquals(steps.get(6), new StepSummaryLine.InitialSummaryLine(task3.getDescriptor()));
        assertEquals(steps.get(7).getId(), task3.getDescriptor().getId());
        assertEquals(((StepSummaryLine.ExecutedSummaryLine) steps.get(7)).getPrettyResult(), true);
        assertEquals(steps.get(8).getId(), task3.getDescriptor().getId());
        assertEquals(((StepSummaryLine.AfterExecutionAuditSummaryLine) steps.get(8)).getPrettyResult(), true);

        //saved states
        ArgumentCaptor<AuditItem> savedStateCaptor = ArgumentCaptor.forClass(AuditItem.class);
        verify(stateSaver, times(3)).writeStep(savedStateCaptor.capture());
        List<AuditItem> savedStates = savedStateCaptor.getAllValues();
        assertEquals(3, savedStates.size());

//        //TASK1
//        //Execution saved(no rolled back)
//        assertTrue(task1.isExecuted());
//        assertFalse(task1.isRollbackExecuted());
//        //stated saved
//        assertEquals(savedStates.get(0).getTask().getDescriptor().getId(), task1.getDescriptor().getId());
//
//        //TASK2
//        //Execution saved(no rolled back)
//        assertTrue(task2.isExecuted());
//        assertFalse(task2.isRollbackExecuted());
//        assertEquals(savedStates.get(1).getTask().getDescriptor().getId(), task2.getDescriptor().getId());
//
//        //TASK3
//        assertTrue(task3.isExecuted());
//        assertFalse(task3.isRollbackExecuted());
//        //Execution saved(no rolled back)
//        assertEquals(savedStates.get(2).getTask().getDescriptor().getId(), task3.getDescriptor().getId());
    }

    @Test
    @DisplayName("GIVEN a non-previously-executed list of tasks\n" +
            "WHEN the processor is executed\n" +
            "AND one intermediate task fails\n" +
            "THEN it should stop executing the rest of the tasks after the failing one")
    void shouldShortCutWhenOneIntermediateTaskFails() {
        //SETUP
        AuditWriter<?> stateSaver = mock(AuditWriter.class);
        when(stateSaver.writeStep(any(AuditItem.class))).thenReturn(new AuditResult.Ok());

        //GIVEN
        SuccessTestExecutableTask task1 = new SuccessTestExecutableTask("task1");
        FailedTestExecutableTask task2 = new FailedTestExecutableTask("task2");
        SuccessTestExecutableTask task3 = new SuccessTestExecutableTask("task3");
        SingleExecutableProcess process = new SingleExecutableProcess(Arrays.asList(task1, task2, task3));

        //WHEN
        ExecutionContext executionContext = new ExecutionContext(null, null, null, null);
        ProcessExecutor.Output output = new SingleProcessExecutor(stateSaver)
                .run(process, executionContext, mock(DefaultRuntimeHelper.class));
        String actualSummary = output.getSummary().getPretty();
        System.out.println(actualSummary);

//        //THEN
//        ArgumentCaptor<AuditItem> savedStateCaptor = ArgumentCaptor.forClass(AuditItem.class);
//        verify(stateSaver, times(3)).writeStep(savedStateCaptor.capture());
//        List<AuditItem> savedStates = savedStateCaptor.getAllValues();
//        assertEquals(3, savedStates.size());
//
//        //TASK1 executed(no rolled back)
//        //execution checked
//        assertTrue(task1.isExecuted());
//        assertFalse(task1.isRollbackExecuted());
//        //state saved
//        TaskStep task1Step1 = savedStates.get(0);
//        assertEquals(task1Step1.getTask().getDescriptor().getId(), task1.getDescriptor().getId());
//        assertEquals(task1Step1.getClass(), SuccessExecutionStep.class);
//
//        //TASK2
//        //execution and rollback checked
//        assertTrue(task2.isExecuted());
//        assertTrue(task2.isRollbackExecuted());
//        //execution saved
//        TaskStep task2Step1 = savedStates.get(1);
//        assertEquals(task2Step1.getTask().getDescriptor().getId(), task2.getDescriptor().getId());
//        assertEquals(task2Step1.getClass(), FailedExecutionStep.class);
//        //rollback saved
//        TaskStep task2Step2 = savedStates.get(2);
//        assertEquals(task2Step2.getTask().getDescriptor().getId(), task2.getDescriptor().getId());
//        assertEquals(task2Step2.getClass(), RolledBackStep.class);
//        assertEquals(task2Step2.getExecutionState(), true);
//
//        //TASK3
//        //no executed neither rolled back
//        assertFalse(task3.isExecuted());
//        assertFalse(task3.isRollbackExecuted());
    }


}