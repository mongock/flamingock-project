package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.api.annotations.template.TemplateConfiguration;

import java.util.Map;

public class TemplatedTaskDescriptor extends ReflectionTaskDescriptor<FlamingockTemplate> {


    private final TemplateConfiguration templateConfiguration;

    public TemplatedTaskDescriptor(String id,
                                   String order,
                                   Class<FlamingockTemplate> templateClass,
                                   boolean runAlways,
                                   boolean transactional,
                                   TemplateConfiguration templateConfiguration) {
        super(id, order, templateClass, runAlways, transactional);
        this.templateConfiguration = templateConfiguration;
    }

    public TemplateConfiguration getTemplateConfiguration() {
        return templateConfiguration;
    }



}
