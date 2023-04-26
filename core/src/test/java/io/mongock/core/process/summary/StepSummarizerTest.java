package io.mongock.core.process.summary;

import io.mongock.core.execution.step.ExecutableStep;
import io.mongock.core.execution.step.complete.CompleteSuccessStep;
import io.mongock.core.execution.step.execution.SuccessExecutionStep;
import io.mongock.core.execution.summary.DefaultStepSummarizer;
import io.mongock.core.execution.summary.StepSummarizer;
import io.mongock.core.execution.summary.StepSummaryLine;
import io.mongock.core.process.stubs.task.SuccessTestExecutableTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepSummarizerTest {

    @Test
    @DisplayName("GIVEN 3 happy-path execution tasks\n" +
            "WHEN the summary is extracted\n" +
            "THEN it should return the right summary")
    void shouldRunAllTasks() {

        //GIVEN
        SuccessTestExecutableTask task1 = new SuccessTestExecutableTask("task1");
        ExecutableStep task1InitialStep = new ExecutableStep(task1);


        //WHEN
        StepSummarizer summarizer = new DefaultStepSummarizer();
        summarizer.add(SuccessExecutionStep.instance(task1InitialStep, -1l));
        summarizer.add(CompleteSuccessStep.fromSuccessExecution(SuccessExecutionStep.instance(task1InitialStep, -1l)));

        List<StepSummaryLine> steps = summarizer.getSummary().getLines();
        //Task1's summary lines
        assertEquals(steps.get(0), new StepSummaryLine.InitialSummaryLine(task1.getDescriptor()));
        assertEquals(steps.get(1).getId(), task1.getDescriptor().getId());
        assertTrue(steps.get(1).isSuccess());
        assertEquals(steps.get(2).getId(), task1.getDescriptor().getId());
        assertTrue(steps.get(2).isSuccess());


    }


}