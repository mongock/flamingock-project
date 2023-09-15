package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;

import java.util.Map;


public class TemplatedTaskDescriptor extends ReflectionTaskDescriptor {


    private final Map<String, Object> templateConfiguration;

    public TemplatedTaskDescriptor(String id,
                                   String order,
                                   Class<?> templateClass,
                                   boolean transactional,
                                   boolean runAlways,
                                   Map<String, Object> templateConfiguration) {
        super(id, order, templateClass, runAlways, transactional);
        this.templateConfiguration = templateConfiguration;
    }

    public Map<String, Object> getTemplateConfiguration() {
        return templateConfiguration;
    }



}
