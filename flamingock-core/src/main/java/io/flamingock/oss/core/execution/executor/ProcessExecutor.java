package io.flamingock.oss.core.execution.executor;

import io.flamingock.oss.core.execution.summary.StepSummary;
import io.flamingock.oss.core.lock.Lock;
import io.flamingock.oss.core.process.ExecutableProcess;

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
