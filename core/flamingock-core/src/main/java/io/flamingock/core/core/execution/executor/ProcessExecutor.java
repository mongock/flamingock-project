package io.flamingock.core.core.execution.executor;

import io.flamingock.core.core.execution.summary.StepSummary;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.process.ExecutableProcess;

public interface ProcessExecutor<EXECUTABLE_PROCESS extends ExecutableProcess> {
    Output run(EXECUTABLE_PROCESS executableProcess,
               ExecutionContext executionContext,
               Lock lock) throws ProcessExecutionException;

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
