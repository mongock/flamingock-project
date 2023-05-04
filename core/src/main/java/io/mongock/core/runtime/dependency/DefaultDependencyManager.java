package io.mongock.core.runtime.dependency;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class DefaultDependencyManager extends AbstractDependencyManager {

    private final LinkedHashSet<Dependency> standardDependencies;

    public DefaultDependencyManager() {
        this.standardDependencies = new LinkedHashSet<>();
    }
    public DefaultDependencyManager addStandardDependencies(Collection<? extends Dependency> dependencies) {
        dependencies.forEach(this::addStandardDependency);
        return this;
    }

    public void addStandardDependency(Dependency dependency) {
        addDependency(standardDependencies, dependency);
    }


    @Override
    protected Optional<Dependency> getStandardDependency(Class<?> type, String name) {
        return getDependencyFromStore(standardDependencies, type, name);
    }

}
