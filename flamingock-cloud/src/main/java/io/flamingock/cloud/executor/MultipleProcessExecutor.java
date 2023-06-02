package io.flamingock.cloud.executor;

import io.flamingock.cloud.process.FlamingockExecutableProcess;
import io.flamingock.core.core.execution.executor.ExecutionContext;
import io.flamingock.core.core.execution.executor.ProcessExecutionException;
import io.flamingock.core.core.execution.executor.ProcessExecutor;
import io.flamingock.core.core.lock.Lock;

public class MultipleProcessExecutor implements ProcessExecutor<FlamingockExecutableProcess> {


    @Override
    public Output run(FlamingockExecutableProcess executableProcess, ExecutionContext executionContext, Lock lock) throws ProcessExecutionException {
        return null;
    }
}
