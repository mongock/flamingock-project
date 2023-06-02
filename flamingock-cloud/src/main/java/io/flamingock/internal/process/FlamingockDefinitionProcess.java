package io.flamingock.internal.process;

import io.flamingock.internal.FlamingockConfiguration;
import io.flamingock.internal.state.FlamingockAuditProcessStatus;
import io.flamingock.oss.core.process.DefinitionProcess;
import io.flamingock.oss.core.process.LoadedProcess;

public class FlamingockDefinitionProcess implements DefinitionProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> {

    private final FlamingockConfiguration config;

    public FlamingockDefinitionProcess(FlamingockConfiguration config) {
        this.config = config;
    }

    @Override
    public LoadedProcess<FlamingockAuditProcessStatus, FlamingockExecutableProcess> load() {
        throw new RuntimeException("NOT  IMPLEMENTED");
    }
}
