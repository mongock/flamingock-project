package io.flamingock.core.spring;

import io.flamingock.core.core.runtime.dependency.Dependency;
import io.flamingock.core.core.runtime.dependency.DependencyContext;
import io.flamingock.core.core.runtime.dependency.exception.ForbiddenParameterException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

public class SpringDependencyContext implements DependencyContext {


    private final ApplicationContext springContext;

    public SpringDependencyContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type) throws ForbiddenParameterException {
        try {
            return Optional.of(new Dependency(type, springContext.getBean(type)));
        } catch (BeansException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Dependency> getDependency(Class<?> type, String name) throws ForbiddenParameterException {
        return Optional.empty();
    }

    @Override
    public List<Dependency> getAllDependencies() {
        return null;
    }
}
