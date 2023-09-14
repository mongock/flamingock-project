package io.flamingock.core.api.annotations.template;

import io.flamingock.core.api.exception.FlamingockException;

public interface FlamingockTemplate {

    String getName();

    void setConfiguration(TemplateConfiguration configuration);

    void validateConfiguration() throws FlamingockException;
}
