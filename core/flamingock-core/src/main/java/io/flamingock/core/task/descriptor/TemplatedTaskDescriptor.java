package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.template.FlamingockTemplate;

import java.util.Map;

public class TemplatedTaskDescriptor extends AbstractTaskDescriptor {


    private final Map<String, Object> templateConfiguration;
    private final FlamingockTemplate template;

    public TemplatedTaskDescriptor(String id,
                                   String order,
                                   FlamingockTemplate template,
                                   boolean runAlways,
                                   boolean transactional,
                                   Map<String, Object> templateConfiguration) {
        super(id, order, runAlways, transactional);
        this.template = template;
        this.templateConfiguration = templateConfiguration;

    }


    public Map<String, Object> getTemplateConfiguration() {
        return templateConfiguration;
    }

    @Override
    public String getSourceName() {
        return template.getName();
    }
}
