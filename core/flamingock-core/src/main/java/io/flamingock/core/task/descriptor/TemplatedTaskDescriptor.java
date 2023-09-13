package io.flamingock.core.task.descriptor;

import java.util.Map;

public class TemplatedTaskDescriptor extends ReflectionTaskDescriptor {


    private final Map<String, Object> templateConfiguration;

    public TemplatedTaskDescriptor(String id, String order,
                                   Class<?> source,
                                   boolean runAlways,
                                   boolean transactional,
                                   Map<String, Object> templateConfiguration) {
        super(id, order, source, runAlways, transactional);
        this.templateConfiguration = templateConfiguration;
    }

    public Map<String, Object> getTemplateConfiguration() {
        return templateConfiguration;
    }
}
