package io.flamingock.cloud.process;

import io.flamingock.cloud.FlamingockConfiguration;
import io.flamingock.cloud.state.FlamingockAuditProcessStatus;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.LoadedProcess;

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
