package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.api.annotations.template.TemplateConfiguration;
import io.flamingock.core.api.annotations.template.TransactionalFlamingockTemplate;


public class TemplatedTaskDescriptor extends ReflectionTaskDescriptor<FlamingockTemplate> {


    private final TemplateConfiguration templateConfiguration;

    public TemplatedTaskDescriptor(String id,
                                   String order,
                                   Class<FlamingockTemplate> templateClass,
                                   boolean runAlways,
                                   TemplateConfiguration templateConfiguration) {
        super(id, order, templateClass, runAlways, TransactionalFlamingockTemplate.class.isAssignableFrom(templateClass));
        this.templateConfiguration = templateConfiguration;
    }

    public TemplateConfiguration getTemplateConfiguration() {
        return templateConfiguration;
    }



}
