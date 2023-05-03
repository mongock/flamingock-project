package io.mongock.core.runtime.dependency;

import java.util.function.Function;


/**
 * This class it to hold dependencies that have as implementation a dependency from the Dependencymanager itself. So they
 * need to retrieve the impl from the dependencyManager
 */
public class DependencyBuildable extends Dependency {

  private final Function<Object, Object> decoratorFunction;
  private final Class<?> implType;

  public DependencyBuildable(Class<?> type, Class<?> implType, Function<Object, Object> decoratorFunction, boolean implProxeable) {
    super(DEFAULT_NAME, type, implProxeable);
    this.decoratorFunction = decoratorFunction;
    this.implType = implType;
  }

  public void setImpl(Object impl) {
    this.instance = impl;
  }

  @Override
  public Object getInstance() {
    return decoratorFunction.apply(instance);
  }


  public Function<Object, Object> getDecoratorFunction() {
    return decoratorFunction;
  }

  public Class<?> getImplType() {
    return implType;
  }
}
