package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.api.annotations.template.TemplateConfiguration;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.template.TemplateFactory;


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

    private String template;

    private TemplateConfiguration templateConfiguration;

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

    public TemplatedTaskDescriptorBuilder setTemplate(String template) {
        this.template = template;
        return this;
    }

    public TemplatedTaskDescriptorBuilder setTemplateConfiguration(TemplateConfiguration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
        return this;
    }

    public TemplatedTaskDescriptor build() {

        Class<FlamingockTemplate> templateClass = TemplateFactory.getTemplate(template)
                .orElseThrow(() -> new FlamingockException("Template not found: " + template));

        return new TemplatedTaskDescriptor(id, order, templateClass, transactional, runAlways, templateConfiguration);

    }
}
