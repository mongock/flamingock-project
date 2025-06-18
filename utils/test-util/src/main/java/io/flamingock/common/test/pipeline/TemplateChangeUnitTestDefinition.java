package io.flamingock.common.test.pipeline;

import io.flamingock.internal.common.core.preview.AbstractPreviewTask;
import io.flamingock.internal.common.core.preview.TemplatePreviewChangeUnit;

import java.util.Collections;
import java.util.Map;

public class TemplateChangeUnitTestDefinition extends ChangeUnitTestDefinition {


    private final String templateName;
    private final Object sharedConfiguration;
    private final Object execution;
    private final Object rollback;


    public TemplateChangeUnitTestDefinition(String id,
                                            String order,
                                            String templateName,
                                            boolean transactional,
                                            Object sharedConfiguration,
                                            Object execution,
                                            Object rollback) {
        super(id, order, transactional);
        this.templateName = templateName;
        this.sharedConfiguration = sharedConfiguration;
        this.execution = execution;
        this.rollback = rollback;
    }


    @Override
    public AbstractPreviewTask toPreview() {
        return new TemplatePreviewChangeUnit(
                getId(),
                getOrder(),
                templateName,
                Collections.emptyList(),
                isTransactional(),
                false,
                false,
                sharedConfiguration,
                execution,
                rollback
        );
    }

}
