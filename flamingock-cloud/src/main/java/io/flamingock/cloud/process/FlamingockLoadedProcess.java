package io.flamingock.cloud.process;

import io.flamingock.cloud.state.FlamingockAuditProcessStatus;
import io.flamingock.core.core.process.LoadedProcess;

public class FlamingockLoadedProcess implements LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {
    @Override
    public FlamingockExecutableProcess applyState(FlamingockAuditProcessStatus state) {
        return new FlamingockExecutableProcess();
    }
}
