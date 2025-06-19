package io.flamingock.internal.common.core.template;

import io.flamingock.api.template.ChangeTemplate;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public final class TemplateManager {

    private static final Logger logger = LoggerFactory.getLogger(TemplateManager.class);

    private static final Map<String, Class<? extends ChangeTemplate<?, ?, ?>>> templates = new HashMap<>();

    private TemplateManager() {
    }


    @SuppressWarnings("unchecked")
    public static void loadTemplates() {
        logger.debug("Registering templates");
        getTemplates().forEach(template -> {
            Class<? extends ChangeTemplate<?, ?, ?>> templateClass = (Class<? extends ChangeTemplate<?, ?, ?>>) template.getClass();
            templates.put(templateClass.getSimpleName(), templateClass);
            logger.debug("registered template: {}", templateClass.getSimpleName());
        });

    }

    @SuppressWarnings("unchecked")
    public static Collection<ChangeTemplate<?, ?, ?>> getTemplates() {
        logger.debug("Retrieving ChangeTemplates");

        //Loads the ChangeTemplates directly registered with SPI
        List<ChangeTemplate<?, ?, ?>> templateClasses = new ArrayList<>();
        for (ChangeTemplate<?, ?, ?> template : ServiceLoader.load(ChangeTemplate.class)) {
            templateClasses.add(template);
        }

        //Loads the ChangeTemplates from the federated ChangeTemplateFactory, registered with SPI
        for (ChangeTemplateFactory factory : ServiceLoader.load(ChangeTemplateFactory.class)) {
            templateClasses.addAll(factory.getTemplates());
        }
        logger.debug("returning ChangeTemplates");

        return templateClasses;
    }


    @TestOnly
    public static void addTemplate(String templateName, Class<? extends ChangeTemplate<?, ?, ?>> templateClass) {
        templates.put(templateName, templateClass);
    }

    public static Optional<Class<? extends ChangeTemplate<?, ?, ?>>> getTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName));
    }
}
