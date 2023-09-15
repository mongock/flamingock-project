package io.flamingock.core.api.annotations.template;

import io.flamingock.core.api.exception.FlamingockException;

import java.util.Map;

public interface FlamingockTemplate {

    void setConfiguration(Map<String, Object> configuration);

    boolean validateConfiguration();
}
