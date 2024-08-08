package io.flamingock.core.configurator;

import flamingock.core.api.Dependency;
import flamingock.core.api.SystemModule;
import io.flamingock.core.pipeline.Stage;

import java.util.List;

public interface SystemModuleManager<T extends SystemModule> {

    void add(T module);

    Iterable<Dependency> getDependencies();

    List<Stage> getSortedSystemStagesAfter();

    List<Stage> getSortedSystemStagesBefore();
}
