package io.flamingock.oss.core.runtime.dependency;

import java.util.Collection;

public abstract class AbstractDependencyInjectableContext extends AbstractDependencyContext implements DependencyInjectableContext {



    protected <T extends Dependency> void addDependencyToStore(Collection<T> dependencyStore, T dependency) {
        //add returns false if it's already there. In that case, it needs to be removed and then inserted
        if (!dependencyStore.add(dependency)) {
            dependencyStore.remove(dependency);
            dependencyStore.add(dependency);
        }
    }

}
