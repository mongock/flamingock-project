package io.mongock.core.runtime.dependency;

import io.mongock.core.runtime.dependency.exception.ForbiddenParameterException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultDependencyContext implements DependencyContext {

    private final LinkedHashSet<Dependency> standardDependencies;

    protected DefaultDependencyContext() {
        standardDependencies = new LinkedHashSet<>();
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        return getDependency(type, null);
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        Optional<Dependency> dependencyOptional = getDependencyFromStore(type, name);
        if (!dependencyOptional.isPresent()) {
            return Optional.empty();

        }
        Dependency dependency = dependencyOptional.get();
        return DependencyBuildable.class.isAssignableFrom(dependency.getClass())
                ? getDependency(((DependencyBuildable) dependency).getImplType())
                : dependencyOptional;
    }


    protected final Optional<Dependency> getDependencyFromStore(Class<?> type, String name) {
        boolean isByName = name != null && !name.isEmpty() && !Dependency.DEFAULT_NAME.equals(name);
        Predicate<Dependency> filter = isByName
                ? dependency -> name.equals(dependency.getName())
                : dependency -> type.isAssignableFrom(dependency.getType());

        Stream<Dependency> stream = standardDependencies.stream().filter(filter);
        if (isByName) {
            return stream.findFirst();
        } else {
            return stream.reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1);
        }
    }

//    protected <T extends Dependency> void addDependency(Collection<T> dependencyStore, T dependency) {
//        //add returns false if it's already there. In that case, it needs to be removed and then inserted
//        if (!dependencyStore.add(dependency)) {
//            dependencyStore.remove(dependency);
//            dependencyStore.add(dependency);
//        }
//    }


}
