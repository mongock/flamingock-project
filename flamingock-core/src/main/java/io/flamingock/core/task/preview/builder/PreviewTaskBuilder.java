package io.flamingock.core.task.preview.builder;

import io.flamingock.core.task.preview.AbstractPreviewTask;
import io.flamingock.core.task.preview.CodePreviewChangeUnit;
import io.flamingock.core.task.preview.TemplatePreviewChangeUnit;
import io.flamingock.core.api.template.ChangeFileDescriptor;

import javax.lang.model.element.TypeElement;

public interface PreviewTaskBuilder<A extends AbstractPreviewTask> {

    static PreviewTaskBuilder<TemplatePreviewChangeUnit> getTemplateBuilder(ChangeFileDescriptor templatedTaskDefinition) {
        return TemplatePreviewTaskBuilder.builder(templatedTaskDefinition);
    }

    static PreviewTaskBuilder<CodePreviewChangeUnit> getCodeBuilder(TypeElement typeElement) {
        return CodePreviewTaskBuilder.builder(typeElement);
    }

    static CodePreviewTaskBuilder getCodeBuilder() {
        return CodePreviewTaskBuilder.builder();
    }

    A build();

}
