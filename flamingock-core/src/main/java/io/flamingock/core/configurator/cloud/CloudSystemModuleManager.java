package io.flamingock.core.configurator.cloud;

import flamingock.core.api.CloudSystemModule;
import flamingock.core.api.Dependency;
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

public class CloudSystemModuleManager implements SystemModuleManager<CloudSystemModule> {

    private final Set<CloudSystemModule> systemModules = new LinkedHashSet<>();

    public void initialize(EnvironmentId environmentId, ServiceId serviceId) {
        systemModules.forEach(m-> m.initialise(environmentId, serviceId));
    }

    @Override
    public void add(CloudSystemModule module) {
        systemModules.add(module);
    }

    @Override
    public Iterable<Dependency> getDependencies() {
        Set<Dependency> dependencies = new HashSet<>();
        systemModules.forEach(m-> dependencies.addAll(m.getDependencies()));
        return new LinkedList<>(dependencies);
    }


    @Override
    public List<Stage> getSortedSystemStagesAfter() {
        return getSortedSystemStages(false);
    }

    @Override
    public List<Stage> getSortedSystemStagesBefore() {
        return getSortedSystemStages(true);
    }

    private List<Stage> getSortedSystemStages(boolean beforeUserStages) {
        List<SystemModule> sortedModules = new ArrayList<>(systemModules);
        Collections.sort(sortedModules);
        Stream<SystemModule> stream = beforeUserStages
                ? sortedModules.stream().filter(SystemModule::isBeforeUserStages)
                : sortedModules.stream().filter(m-> !m.isBeforeUserStages());
        return stream
                .map(m -> new Stage(m.getName()).setClasses(m.getTaskClasses()))
                .collect(Collectors.toList());
    }
}
