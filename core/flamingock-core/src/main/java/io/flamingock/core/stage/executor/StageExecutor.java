package io.flamingock.core.stage.executor;

import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.stage.ExecutableStage;

public interface StageExecutor {
    Output run(ExecutableStage executableProcess,
               StageExecutionContext stageExecutionContext,
               Lock lock) throws StageExecutionException;

    class Output {

        private final StepSummary summary;

        public Output(StepSummary summary) {
            this.summary = summary;
        }

        public StepSummary getSummary() {
            return summary;
        }
    }
}
