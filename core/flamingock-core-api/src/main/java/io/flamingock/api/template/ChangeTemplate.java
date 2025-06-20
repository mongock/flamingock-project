package io.flamingock.api.template;

import io.flamingock.api.task.TaskCategory;

import java.util.Collection;

/**
 * Interface representing a reusable change template with configuration of type {@code CONFIG}.
 * <p>
 * This interface is commonly implemented by classes that act as templates for Change Units
 * where a specific configuration needs to be injected and managed independently.
 */
public interface ChangeTemplate<SHARED_CONFIG, EXECUTION, ROLLBACK> extends ReflectionMetadataProvider {

    void setChangeId(String changeId);

    void setTransactional(boolean isTransactional);

    void setConfiguration(SHARED_CONFIG configuration);

    void setExecution(EXECUTION execution);

    void setRollback(ROLLBACK rollback);

    Class<SHARED_CONFIG> getConfigurationClass();

    Class<EXECUTION> getExecutionClass();

    Class<ROLLBACK> getRollbackClass();

    Collection<TaskCategory> getCategories();

}
