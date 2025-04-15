package io.flamingock.core.api.template;

import io.flamingock.core.api.metadata.ReflectionMetadataProvider;

public interface ChangeTemplate<CONFIG> extends ReflectionMetadataProvider {

    void setConfiguration(CONFIG config);

}
