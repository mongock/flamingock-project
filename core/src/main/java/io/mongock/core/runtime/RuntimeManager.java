package io.mongock.core.runtime;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.api.annotations.ChangeUnitConstructor;
import io.mongock.api.exception.CoreException;
import io.mongock.core.lock.Lock;
import io.mongock.core.runtime.dependency.AbstractDependencyManager;
import io.mongock.core.runtime.dependency.Dependency;
import io.mongock.core.runtime.dependency.DependencyContext;
import io.mongock.core.runtime.dependency.DependencyInjector;
import io.mongock.core.runtime.dependency.exception.DependencyInjectionException;
import io.mongock.core.runtime.proxy.LockGuardProxyFactory;
import io.mongock.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RuntimeManager implements DependencyInjector {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeManager.class);


    public static Generator generator() {
        return new Generator();
    }

    private static final Function<Parameter, String> parameterNameProvider = parameter -> parameter.isAnnotationPresent(Named.class)
            ? parameter.getAnnotation(Named.class).value()
            : null;
    private final Set<Class<?>> nonProxyableTypes = Collections.emptySet();
    private final DependencyContext dependencyManager;
    private final LockGuardProxyFactory proxyFactory;

    public RuntimeManager(LockGuardProxyFactory proxyFactory,
                          DependencyContext DependencyContext) {
        this.dependencyManager = DependencyContext;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public void addPriorityDependencies(Collection<? extends Dependency> dependencies) {
//        dependencyManager.addPriorityDependencies(dependencies);
    }

    @Override
    public void addPriorityDependency(Dependency dependency) {
//        dependencyManager.addPriorityDependency(dependency);
    }

    public Object getInstance(Class<?> type) {
        Constructor<?> constructor = getConstructor(type);
        List<Object> signatureParameters = getSignatureParameters(constructor);
        logMethodWithArguments(constructor.getName(), signatureParameters);
        try {
            return constructor.newInstance(signatureParameters.toArray());
        } catch (Exception e) {
            throw new CoreException(e);
        }
    }

    public Object executeMethod(Object instance, Method method) {
        List<Object> signatureParameters = getSignatureParameters(method);
        try {
            return method.invoke(instance, signatureParameters.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getConstructor(Class<?> type) {

        List<Constructor<?>> annotatedConstructors = Arrays.stream(type.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ChangeUnitConstructor.class))
                .collect(Collectors.toList());
        if (annotatedConstructors.size() == 1) {
            return annotatedConstructors.get(0);

        } else if (annotatedConstructors.size() == 0) {
            logger.debug("Not found constructor for class[{}] annotated with {}", type.getName(), ChangeUnitConstructor.class.getSimpleName());
            Constructor<?>[] constructors = type.getConstructors();
            if (constructors.length == 0) {
                throw new CoreException("Cannot find a valid constructor for class[%s]", type.getName());
            }
            if (constructors.length > 1) {
                throw new CoreException("Found multiple constructors without annotation %s  for class[%s].\n" +
                        "When more than one constructor, exactly one of them must be annotated. And it will be taken as default "
                        , ChangeUnitConstructor.class.getSimpleName()
                        , type.getName()
                );
            }
            return constructors[0];

        } else {
            //annotatedConstructors.size() > 1
            throw new CoreException("Found multiple constructors for class[%s] annotated with %s." +
                    " Annotate the one you want Mongock to use to instantiate your changeUnit",
                    type.getName(),
                    ChangeUnitConstructor.class.getSimpleName());
        }
    }

    private List<Object> getSignatureParameters(Executable executable) {
        Class<?>[] parameterTypes = executable.getParameterTypes();
        Parameter[] parameters = executable.getParameters();
        List<Object> signatureParameters = new ArrayList<>(parameterTypes.length);
        for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
            signatureParameters.add(getParameter(parameterTypes[paramIndex], parameters[paramIndex]));
        }
        return signatureParameters;
    }

    private Object getParameter(Class<?> parameterType, Parameter parameter) {
        String parameterName = getParameterName(parameter);
        Dependency dependency = dependencyManager.getDependency(parameterType, parameterName)
                .orElseThrow(() -> new DependencyInjectionException(parameterType, parameterName));

        boolean lockGuarded = !parameterType.isAnnotationPresent(NonLockGuarded.class)
                && !parameter.isAnnotationPresent(NonLockGuarded.class)
                && !nonProxyableTypes.contains(parameterType);

        return dependency.isProxeable() && lockGuarded
                ? proxyFactory.getRawProxy(dependency.getInstance(), dependency.getType())
                : dependency.getInstance();

    }

    private String getParameterName(Parameter parameter) {
        return parameterNameProvider.apply(parameter);
    }


    public static void logMethodWithArguments(String methodName, List<Object> changelogInvocationParameters) {
        String arguments = changelogInvocationParameters.stream()
                .map(RuntimeManager::getParameterType)
                .collect(Collectors.joining(", "));
        logger.info("method[{}] with arguments: [{}]", methodName, arguments);

    }

    private static String getParameterType(Object obj) {
        String className = obj != null ? obj.getClass().getName() : "{null argument}";
        int mongockProxyPrefixIndex = className.indexOf(Constants.PROXY_MONGOCK_PREFIX);
        if (mongockProxyPrefixIndex > 0) {
            return className.substring(0, mongockProxyPrefixIndex);
        } else {
            return className;
        }
    }

    public static final class Generator {

        private DependencyContext dependencyContext;
        private Lock lock;

        public Generator setLock(Lock lock) {
            this.lock = lock;
            return this;
        }

        public Generator setDependencyContext(DependencyContext dependencyContext) {
            this.dependencyContext = dependencyContext;
            return this;
        }

        public RuntimeManager generate() {
            LockGuardProxyFactory proxyFactory = new LockGuardProxyFactory(lock);
            return new RuntimeManager(proxyFactory, dependencyContext);
        }
    }


}
