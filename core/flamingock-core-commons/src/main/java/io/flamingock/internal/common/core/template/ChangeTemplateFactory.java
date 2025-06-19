package io.flamingock.internal.common.core.template;

import io.flamingock.api.template.ChangeTemplate;

import java.util.Collection;

public interface ChangeTemplateFactory {

    Collection<ChangeTemplate<?, ?, ?>> getTemplates();
}
