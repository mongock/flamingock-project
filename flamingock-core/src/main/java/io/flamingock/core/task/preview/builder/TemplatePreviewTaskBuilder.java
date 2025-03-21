/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.task.preview.builder;

import io.flamingock.core.task.preview.TemplatePreviewChangeUnit;
import io.flamingock.template.TemplateSpec;
import io.flamingock.template.TemplateTaskDefinition;
import io.flamingock.template.TransactionalTemplateSpec;

import java.util.Map;


//TODO how to set transactional and runAlways
public class TemplatePreviewTaskBuilder {

    private static final String TEMPLATE_NOT_FOUND_MSG = "Template [%s] not found. Ensure that the template's library is correctly imported and that the template class field in your templated ChangeUnit specifies the correct class path";

    private static final TemplatePreviewTaskBuilder instance = new TemplatePreviewTaskBuilder();

    public static TemplatePreviewTaskBuilder recycledBuilder() {
        return instance;
    }

    private String id;

    private String order;

    private String templateClassPath;

    private boolean runAlways;

    private Boolean transactional;

    private Map<String, Object> templateConfiguration;

    private TemplatePreviewTaskBuilder() {
    }

    public TemplatePreviewTaskBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TemplatePreviewTaskBuilder setOrder(String order) {
        this.order = order;
        return this;
    }

    public void setTemplateClassPath(String templateClassPath) {
        this.templateClassPath = templateClassPath;
    }


    public TemplatePreviewTaskBuilder setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
        return this;
    }

    public TemplatePreviewTaskBuilder setTransactional(Boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    public TemplatePreviewTaskBuilder setTemplateConfiguration(Map<String, Object> templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
        return this;
    }

    public TemplatePreviewChangeUnit getPreview() {
        return new TemplatePreviewChangeUnit(id, order, templateClassPath, transactional, runAlways, templateConfiguration);
    }



    public TemplatePreviewTaskBuilder setFromDefinition(TemplateTaskDefinition templateTaskDefinition) {
        setId(templateTaskDefinition.getId());
        setOrder(templateTaskDefinition.getOrder());
        setTemplateClassPath(templateTaskDefinition.getTemplate());
        setTemplateConfiguration(templateTaskDefinition.getTemplateConfiguration());
        setTransactional(templateTaskDefinition.getTransactional());
        setRunAlways(false);
        return this;
    }

    private boolean isTaskTransactionalAccordingTemplate(TemplateSpec templateSpec) {
        boolean isTemplateTransactional = TransactionalTemplateSpec.class.isAssignableFrom(templateSpec.getClass());
        if(!isTemplateTransactional) {
            return false;
        } else {
            return transactional != null ? transactional : true;
        }
    }

    public void reset() {
        setId(null);
        setOrder(null);
        setTemplateClassPath(null);
        setTemplateConfiguration(null);
        setTransactional(false);
        setRunAlways(false);
    }
}
