package io.flamingock.springboot.v2;

import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.runtime.dependency.DependencyContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Optional;

public class SpringbootV2DependencyContext implements DependencyContext {

    private final ApplicationContext applicationContext;
    private  final Environment environment;

    public SpringbootV2DependencyContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
    }


    @Override
    public Optional<Dependency> getDependency(Class<?> type) {
        try {
            return Optional.of(new Dependency(type, applicationContext.getBean(type)));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Dependency> getDependency(String name) {
        try {
            return Optional.of(new Dependency(applicationContext.getBean(name)));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(environment.getProperty(key));

    }

    @Override
    public <T> Optional<T> getPropertyAs(String key, Class<T> type) {
        return Optional.ofNullable(environment.getProperty(key, type));
    }
}
