package io.flamingock.core.runner;

import io.flamingock.core.pipeline.ExecutablePipeline;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.execution.StageSummary;
import io.flamingock.core.task.executable.ExecutableTask;
import io.flamingock.core.task.navigation.summary.DefaultStepSummarizer;
import io.flamingock.core.task.navigation.summary.StepSummary;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class PipelineSummary implements StepSummary {

    private final LinkedHashMap<String, StageSummary> stageSummaries = new LinkedHashMap<>();

    public PipelineSummary(ExecutablePipeline pipeline) {

        for (ExecutableStage executableStage : pipeline.getExecutableStages()) {
            StageSummary stageSummary = new StageSummary(executableStage.getName());

            for (ExecutableTask executableTask : executableStage.getTasks()) {
                stageSummary.addSummary(new DefaultStepSummarizer()
                        .addNotReachedTask(executableTask.getDescriptor())
                        .getSummary());
            }
            stageSummaries.put(executableStage.getName(), stageSummary);
        }
    }

    public void merge(StageSummary stageSummary) {

        stageSummaries.put(stageSummary.getId(), stageSummary);
    }

    public void put(StageSummary stageSummary) {
        stageSummaries.put(stageSummary.getId(), stageSummary);
    }



    @Override
    public List<StageSummary> getLines() {
        return new LinkedList<>(stageSummaries.values());
    }
}
