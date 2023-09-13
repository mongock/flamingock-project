package io.flamingock.core.template;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;

import java.util.Optional;

public final class TemplateFactory {

    private TemplateFactory() {
    }

    public static Optional<FlamingockTemplate> getTemplate(String templateName) {
        return Optional.empty();
    }
 }
