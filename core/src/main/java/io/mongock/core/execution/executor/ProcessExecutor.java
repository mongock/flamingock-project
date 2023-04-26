package io.mongock.core.execution.executor;

import io.mongock.core.execution.summary.StepSummary;
import io.mongock.core.process.ExecutableProcess;

public interface ProcessExecutor<EXECUTABLE_PROCESS extends ExecutableProcess> {
    Output run(EXECUTABLE_PROCESS executableProcess, ExecutionContext executionContext);

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
