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

package io.flamingock.internal.core.task.loaded;

import io.flamingock.internal.common.core.error.FlamingockException;
import io.flamingock.api.template.ChangeTemplate;
import io.flamingock.internal.common.core.template.ChangeTemplateManager;
import io.flamingock.internal.common.core.preview.AbstractPreviewTask;
import io.flamingock.internal.common.core.preview.TemplatePreviewChangeUnit;

import java.util.List;


//TODO how to set transactional and runAlways
public class TemplateLoadedTaskBuilder implements LoadedTaskBuilder<TemplateLoadedChangeUnit> {

    private String fileName;
    private String id;
    private String orderInContent;
    private String templateName;
    private List<String> profiles;
    private boolean runAlways;
    private boolean transactional;
    private boolean system;
    private Object configuration;
    private Object execution;
    private Object rollback;

    private TemplateLoadedTaskBuilder() {
    }

    static TemplateLoadedTaskBuilder getInstance() {
        return new TemplateLoadedTaskBuilder();
    }

    static TemplateLoadedTaskBuilder getInstanceFromPreview(TemplatePreviewChangeUnit preview) {
        return getInstance().setPreview(preview);
    }

    public static boolean supportsPreview(AbstractPreviewTask previewTask) {
        return TemplatePreviewChangeUnit.class.isAssignableFrom(previewTask.getClass());
    }

    public TemplateLoadedTaskBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TemplateLoadedTaskBuilder setOrderInContent(String order) {
        this.orderInContent = order;
        return this;
    }

    public TemplateLoadedTaskBuilder setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public TemplateLoadedTaskBuilder setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
        return this;
    }

    public TemplateLoadedTaskBuilder setTransactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    public TemplateLoadedTaskBuilder setSystem(boolean system) {
        this.system = system;
        return this;
    }

    public TemplateLoadedTaskBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public TemplateLoadedTaskBuilder setConfiguration(Object configuration) {
        this.configuration = configuration;
        return this;
    }

    public TemplateLoadedTaskBuilder setExecution(Object execution) {
        this.execution = execution;
        return this;
    }

    public TemplateLoadedTaskBuilder setRollback(Object rollback) {
        this.rollback = rollback;
        return this;
    }

    @Override
    public TemplateLoadedChangeUnit build() {
        //            boolean isTaskTransactional = true;//TODO implement this. isTaskTransactionalAccordingTemplate(templateSpec);
        Class<? extends ChangeTemplate<?, ?, ?>> templateClass = ChangeTemplateManager.getTemplate(templateName)
                .orElseThrow(()-> new FlamingockException(String.format("Template[%s] not found. This is probably because template's name is wrong or template's library not imported", templateName)));
        
        String order = LoadedChangeUnitUtil.getMatchedOrderFromFile(id, orderInContent, fileName);
        
        return new TemplateLoadedChangeUnit(
                fileName,
                id,
                order,
                templateClass,
                profiles,
                transactional,
                runAlways,
                system,
                configuration,
                execution,
                rollback);

    }

    private TemplateLoadedTaskBuilder setPreview(TemplatePreviewChangeUnit preview) {
        setFileName(preview.getFileName());
        setId(preview.getId());
        setOrderInContent(preview.getOrder().orElse(null));
        setTemplateName(preview.getTemplateName());
        setProfiles(preview.getProfiles());
        setRunAlways(preview.isRunAlways());
        setTransactional(preview.isTransactional());
        setSystem(preview.isSystem());
        setConfiguration(preview.getConfiguration());
        setExecution(preview.getExecution());
        setRollback(preview.getRollback());
        return this;
    }

}
