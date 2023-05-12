package io.mongock.core.runtime.dependency;


import io.mongock.api.exception.CoreException;

import java.util.Objects;

public class Dependency {

  public static final String DEFAULT_NAME = "default_name_not_used";

  private final String name;
  private final Class<?> type;
  private final boolean proxeable;

  protected Object instance;


  public Dependency(Object instance) {
    this(instance.getClass(), instance);
  }

  public Dependency(Class<?> type, Object instance) {
    this(type, instance, true);
  }
  
  public Dependency(Class<?> type, Object instance, boolean proxeable) {
    this(DEFAULT_NAME, type, instance, proxeable);
  }
  
  public Dependency(String name, Class<?> type, Object instance) {
    this(name, type, instance, true);
  }

  public Dependency(String name, Class<?> type, Object instance, boolean proxeable) {
    this(name, type, proxeable);
    if (instance == null) {
      throw new CoreException("dependency instance cannot be null");
    }
    this.instance = instance;
  }

  protected Dependency(String name, Class<?> type, boolean proxeable) {
    checkParameters(name, type);
    this.name = name;
    this.type = type;
    this.proxeable = proxeable;
  }


  private void checkParameters(String name, Class<?> type) {
    if (name == null || name.isEmpty()) {
      throw new CoreException("dependency name cannot be null/empty");
    }
    if (type == null) {
      throw new CoreException("dependency type cannot be null");
    }
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  public boolean isDefaultNamed() {
    return DEFAULT_NAME.equals(name);
  }
  
  public boolean isProxeable() {
    return proxeable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Dependency)) return false;
    Dependency that = (Dependency) o;
    return name.equals(that.name) && type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }
}
