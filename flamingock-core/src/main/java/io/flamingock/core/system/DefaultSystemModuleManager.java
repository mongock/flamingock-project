package io.flamingock.core.system;

import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultSystemModuleManager implements SystemModuleManager {

    private final Set<SystemModule> systemModules = new LinkedHashSet<>();

    @Override
    public void initialize() {
        systemModules.forEach(SystemModule::initialise);
    }

    @Override
    public void add(SystemModule module) {
        systemModules.add(module);
    }

    @Override
    public Iterable<SystemModule> getModules() {
        return systemModules;
    }

}
