package io.flamingock.api.template;

import io.flamingock.internal.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class AbstractChangeTemplate<SHARED, EXECUTION, ROLLBACK> implements ChangeTemplate<SHARED, EXECUTION, ROLLBACK> {

    private final Class<SHARED> sharedConfigurationClass;
    private final Class<EXECUTION> executionClass;
    private final Class<ROLLBACK> rollbackClass;
    protected String changeId;
    protected boolean isTransactional;
    protected SHARED sharedConfiguration;
    protected EXECUTION execution;
    protected ROLLBACK rollback;


    private final Set<Class<?>> reflectiveClasses;


    @SuppressWarnings("unchecked")
    public AbstractChangeTemplate(Class<?>... additionalReflectiveClass) {
        reflectiveClasses = new HashSet<>(Arrays.asList(additionalReflectiveClass));
        Type[] typeArgs = ReflectionUtil.getActualTypeArguments(this.getClass());
        this.sharedConfigurationClass = (Class<SHARED>) typeArgs[0];
        this.executionClass = (Class<EXECUTION>) typeArgs[1];
        this.rollbackClass = (Class<ROLLBACK>) typeArgs[2];
        reflectiveClasses.add(sharedConfigurationClass);
        reflectiveClasses.add(executionClass);
        reflectiveClasses.add(rollbackClass);
    }

    @Override
    public final Collection<Class<?>> getReflectiveClasses() {
        return reflectiveClasses;
    }

    @Override
    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

    @Override
    public void setTransactional(boolean isTransactional) {
        this.isTransactional = isTransactional;
    }

    @Override
    public void setSharedConfiguration(SHARED configuration) {
        this.sharedConfiguration = configuration;
    }

    @Override
    public void setExecution(EXECUTION execution) {
        this.execution = execution;
    }

    @Override
    public void setRollback(ROLLBACK rollback) {
        this.rollback = rollback;
    }

    @Override
    public Class<SHARED> getSharedConfigurationClass() {
        return sharedConfigurationClass;
    }

    @Override
    public Class<EXECUTION> getExecutionClass() {
        return executionClass;
    }

    @Override
    public Class<ROLLBACK> getRollbackClass() {
        return rollbackClass;
    }

}
