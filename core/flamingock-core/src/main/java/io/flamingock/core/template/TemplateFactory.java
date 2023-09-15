package io.flamingock.core.template;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TemplateFactory {

    private static Map<String, Class<?>> templates = new HashMap<>();

    private TemplateFactory() {
    }

    public static void addTemplate(String templateName, Class<?> templateClass) {
        templates.put(templateName, templateClass);
    }



    public static Optional<Class<?>> getTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName));
    }
 }
