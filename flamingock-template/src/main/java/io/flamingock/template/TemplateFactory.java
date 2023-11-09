package io.flamingock.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TemplateFactory {

    private static final Map<String, Class<?>> templates = new HashMap<>();

    private TemplateFactory() {
    }

    public static void registerModule(TemplateModule templateModule) {
        templateModule.getTemplates().forEach(TemplateFactory::registerTemplate);
    }

    public static void registerTemplate(String templateName, Class<?> templateClass) {
        templates.put(templateName, templateClass);
    }

    public static void registerTemplate(TemplateSpec templateSpec) {
        registerTemplate(templateSpec.getName(), templateSpec.getTemplateClass());
    }


    public static Optional<Class<?>> getTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName));
    }

}
