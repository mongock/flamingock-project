package io.flamingock.core.api.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

//TODO we could store/use objects, rather than Classes
public final class TemplateFactory {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFactory.class);

    private static final Map<String, Class<? extends ChangeTemplate<?>>> templates = new HashMap<>();

    private TemplateFactory() {
    }

    @SuppressWarnings("unchecked")
    public static void loadTemplates() {
        logger.debug("Registering templates");
        for (ChangeTemplate<?> template : ServiceLoader.load(ChangeTemplate.class)) {
            Class<? extends ChangeTemplate<?>> templateClass = (Class<? extends ChangeTemplate<?>>)template.getClass();
            templates.put(templateClass.getSimpleName(), templateClass);
            logger.debug("registered template: {}", templateClass.getSimpleName());
        }
    }

    public static void addTemplate(String templateName, Class<? extends ChangeTemplate<?>> templateClass) {
        templates.put(templateName, templateClass);
    }

    public static Optional<Class<? extends ChangeTemplate<?>>> getTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName));
    }
}
