package io.flamingock.api.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractChangeTemplate<SHARED, EXECUTION, ROLLBACK> implements ChangeTemplate<SHARED, EXECUTION, ROLLBACK> {

    private final Logger logger = LoggerFactory.getLogger("AbstractChangeTemplate");

    protected String changeId;
    protected boolean isTransactional;
    protected ChangeTemplateConfig<SHARED, EXECUTION, ROLLBACK> configuration;

    private final Set<Class<?>> reflectiveClasses;


    public AbstractChangeTemplate(Class<?>... additionalReflectiveClass) {
        reflectiveClasses = new HashSet<>(Arrays.asList(additionalReflectiveClass));
        reflectiveClasses.add(ChangeTemplateConfig.class);

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
    public <CONFIG extends ChangeTemplateConfig<SHARED, EXECUTION, ROLLBACK>> void setConfiguration(CONFIG configuration) {
        logger.trace("setting {} config[{}]: {}", getClass(), getConfigClass(), configuration);
        this.configuration = configuration;
    }

    @Override
    public void setTransactional(boolean isTransactional) {
        this.isTransactional = isTransactional;
    }
}
