package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.template.TemplatedTaskDefinition;
import io.flamingock.core.template.TemplateFactory;

import java.util.Map;


//TODO how to set transactional and runAlways
public class TemplatedTaskDescriptorBuilder {
    private static final TemplatedTaskDescriptorBuilder instance = new TemplatedTaskDescriptorBuilder();

    public static TemplatedTaskDescriptorBuilder recycledBuilder() {
        return instance;
    }

    private String id;

    private String order;

    private boolean runAlways;

    private boolean transactional;

    private String templateName;

    private Map<String, Object> templateConfiguration;

    private TemplatedTaskDescriptorBuilder() {
    }

    public TemplatedTaskDescriptorBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setOrder(String order) {
        this.order = order;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setTransactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setTemplateConfiguration(Map<String, Object> templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
        return this;
    }

    public TemplatedTaskDescriptor build() {

        Class<?> templateClass = TemplateFactory.getTemplate(templateName)
                .orElseThrow(() -> new FlamingockException("Template not found: " + templateName));

        return new TemplatedTaskDescriptor(id, order, templateClass, transactional, runAlways, templateConfiguration);

    }

    public TemplatedTaskDescriptorBuilder setFromDefinition(TemplatedTaskDefinition templatedTaskDefinition) {
        setId(templatedTaskDefinition.getId());
        setOrder(templatedTaskDefinition.getOrder());
        setTemplateName(templatedTaskDefinition.getTemplateName());
        setTemplateConfiguration(templatedTaskDefinition.getConfiguration());
//        setTransactional(templateYaml.getTransactional());
//        setRunAlways(templateYaml.getRunAlways());
        return this;
    }
}
