package io.flamingock.template;

public class TemplateSpec {

    private final String name;

    private final Class<?> templateClass;

    public TemplateSpec(String name, Class<?> templateClass) {
        this.name = name;
        this.templateClass = templateClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getTemplateClass() {
        return templateClass;
    }
}
