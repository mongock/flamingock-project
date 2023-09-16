package io.flamingock.template;

import java.util.Map;

public class TemplatedTaskDefinition {
    private String id;

    private String order;

    private String templateName;

    private Map<String, Object> templateConfiguration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Object> getTemplateConfiguration() {
        return templateConfiguration;
    }

    public void setTemplateConfiguration(Map<String, Object> templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }
}
