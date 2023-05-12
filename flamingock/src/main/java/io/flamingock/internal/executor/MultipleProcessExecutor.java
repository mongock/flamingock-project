package io.flamingock.internal.executor;

import io.flamingock.internal.process.FlamingockExecutableProcess;
import io.mongock.core.execution.executor.ExecutionContext;
import io.mongock.core.execution.executor.ProcessExecutionException;
import io.mongock.core.execution.executor.ProcessExecutor;
import io.mongock.core.lock.Lock;

public class MultipleProcessExecutor implements ProcessExecutor<FlamingockExecutableProcess> {


    @Override
    public Output run(FlamingockExecutableProcess executableProcess, ExecutionContext executionContext, Lock lock) throws ProcessExecutionException {
        return null;
    }
}
