package io.flamingock.core.configurator.local;

import flamingock.core.api.CloudSystemModule;
import flamingock.core.api.Dependency;
import flamingock.core.api.LocalSystemModule;
import flamingock.core.api.SystemModule;
import io.flamingock.commons.utils.id.EnvironmentId;
import io.flamingock.commons.utils.id.ServiceId;
import io.flamingock.core.configurator.SystemModuleManager;
import io.flamingock.core.pipeline.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalSystemModuleManager implements SystemModuleManager<LocalSystemModule> {

    private final Set<LocalSystemModule> systemModules = new LinkedHashSet<>();

    public void initialize() {
        systemModules.forEach(LocalSystemModule::initialise);
    }

    @Override
    public void add(LocalSystemModule module) {
        systemModules.add(module);
    }

    @Override
    public Iterable<LocalSystemModule> getModules() {
        return systemModules;
    }

}
