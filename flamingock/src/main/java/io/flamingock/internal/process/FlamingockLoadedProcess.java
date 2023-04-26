package io.flamingock.internal.process;

import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.mongock.core.process.LoadedProcess;

public class FlamingockLoadedProcess implements LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {
    @Override
    public FlamingockExecutableProcess applyState(FlamingockAuditProcessStatus state) {
        return new FlamingockExecutableProcess();
    }
}
