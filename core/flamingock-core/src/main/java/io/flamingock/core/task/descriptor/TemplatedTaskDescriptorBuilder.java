package io.flamingock.core.task.descriptor;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.template.FlamingockTemplate;
import io.flamingock.core.api.annotations.template.TemplateConfiguration;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.template.TemplateFactory;

public class TemplatedTaskDescriptorBuilder {
    private static final TemplatedTaskDescriptorBuilder instance = new TemplatedTaskDescriptorBuilder();

    public static TemplatedTaskDescriptorBuilder recycledBuilder() {
        return instance;
    }

    private String id;

    private String order;

    private String template;

    private TemplateConfiguration templateConfiguration;

    private TemplatedTaskDescriptorBuilder() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTemplateConfiguration(TemplateConfiguration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }

    public ReflectionTaskDescriptor build() {

        FlamingockTemplate templateClass = TemplateFactory.getTemplate(template)
                .orElseThrow(() -> new FlamingockException("Template not found: " + template));


        return null;
    }



    private static boolean isChangeUnit(Class<?> source) {
        return source.isAnnotationPresent((ChangeUnit.class));
    }

    private static ReflectionTaskDescriptor getDescriptorFromChangeUnit(Class<?> source) {
        ChangeUnit changeUnitAnnotation = source.getAnnotation(ChangeUnit.class);

        return new ReflectionTaskDescriptor(
                changeUnitAnnotation.id(),
                changeUnitAnnotation.order(),
                source,
                changeUnitAnnotation.runAlways(),
                changeUnitAnnotation.transactional());
    }
}
