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

import io.flamingock.commons.utils.StringUtil;
import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.preview.AbstractPreviewTask;
import io.flamingock.core.preview.CodePreviewChangeUnit;
import io.flamingock.core.preview.CodePreviewLegacyChangeUnit;
import io.flamingock.internal.core.utils.ExecutionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeLoadedTaskBuilder implements LoadedTaskBuilder<CodeLoadedChangeUnit> {

    private static final Logger logger = LoggerFactory.getLogger(CodeLoadedTaskBuilder.class);

    private String id;
    private String order;
    private String source;
    private boolean isRunAlways;
    private boolean isTransactional;
    private boolean isSystem;
    private boolean isNewChangeUnit;
    private boolean isBeforeExecution;//only for old change units

    private CodeLoadedTaskBuilder() {
    }

    static CodeLoadedTaskBuilder getInstance() {
        return new CodeLoadedTaskBuilder();
    }

    static CodeLoadedTaskBuilder getInstanceFromPreview(CodePreviewChangeUnit preview) {
        return getInstance().setPreview(preview);
    }

    static CodeLoadedTaskBuilder getInstanceFromClass(Class<?> sourceClass) {
        return getInstance().setSourceClass(sourceClass);
    }

    public static boolean supportsPreview(AbstractPreviewTask previewTask) {
        return CodePreviewChangeUnit.class.isAssignableFrom(previewTask.getClass());
    }

    public static boolean supportsSourceClass(Class<?> sourceClass) {
        return sourceClass.isAnnotationPresent(ChangeUnit.class) || sourceClass.isAnnotationPresent(io.mongock.api.annotations.ChangeUnit.class);

    }


    private CodeLoadedTaskBuilder setPreview(CodePreviewChangeUnit preview) {
        setId(preview.getId());
        setOrder(preview.getOrder().orElse(null));
        setTemplateName(preview.getSource());
        setRunAlways(preview.isRunAlways());
        setTransactional(preview.isTransactional());
        setLegacyChangeUnit(preview instanceof CodePreviewLegacyChangeUnit);
        setSystem(preview.isSystem());
        return this;
    }

    private void setLegacyChangeUnit(boolean legacyChangeUnit) {
        setNewChangeUnit(!legacyChangeUnit);
    }

    private CodeLoadedTaskBuilder setSourceClass(Class<?> sourceClass) {
        if (sourceClass.isAnnotationPresent(ChangeUnit.class)) {
            setFromFlamingockChangeAnnotation(sourceClass, sourceClass.getAnnotation(ChangeUnit.class));
            return this;

        } else if (ExecutionUtils.isLegacyChangeUnit(sourceClass)) {
            setFromLegacyChangeUnitAnnotation(sourceClass, sourceClass.getAnnotation(io.mongock.api.annotations.ChangeUnit.class));
            return this;

        } else {
            throw new IllegalArgumentException(String.format(
                    "Change class[%s] should be annotate with %s",
                    sourceClass.getName(),
                    ChangeUnit.class.getName()
            ));
        }
    }

    public CodeLoadedTaskBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public CodeLoadedTaskBuilder setOrder(String order) {
        this.order = order;
        return this;
    }

    public CodeLoadedTaskBuilder setTemplateName(String templateName) {
        this.source = templateName;
        return this;
    }

    public CodeLoadedTaskBuilder setRunAlways(boolean runAlways) {
        this.isRunAlways = runAlways;
        return this;
    }

    public CodeLoadedTaskBuilder setTransactional(boolean transactional) {
        this.isTransactional = transactional;
        return this;
    }

    public CodeLoadedTaskBuilder setSystem(boolean system) {
        this.isSystem = system;
        return this;
    }

    public CodeLoadedTaskBuilder setNewChangeUnit(boolean newChangeUnit) {
        this.isNewChangeUnit = newChangeUnit;
        return this;
    }

    public CodeLoadedTaskBuilder setBeforeExecution(boolean beforeExecution) {
        isBeforeExecution = beforeExecution;
        return this;
    }

    @Override
    public CodeLoadedChangeUnit build() {

        try {
            return new CodeLoadedChangeUnit(
                    isBeforeExecution ? StringUtil.getBeforeExecutionId(id) : id,
                    order,
                    Class.forName(source),
                    isRunAlways,
                    isTransactional,
                    isNewChangeUnit,
                    isSystem
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFromLegacyChangeUnitAnnotation(Class<?> sourceClass, io.mongock.api.annotations.ChangeUnit annotation) {
        logger.warn("Detected legacy changeUnit[{}]. If it's an old changeUnit created for Mongock, it's fine. " +
                        "Otherwise, it's highly recommended us the new API[in package {}]",
                sourceClass.getName(),
                "io.flamingock.core.api.annotations");
        setId(annotation.id());
        setOrder(annotation.order());
        setTemplateName(sourceClass.getName());
        setRunAlways(annotation.runAlways());
        setTransactional(annotation.transactional());
        setNewChangeUnit(false);
        setSystem(false);
    }

    private void setFromFlamingockChangeAnnotation(Class<?> sourceClass, ChangeUnit annotation) {
        setId(annotation.id());
        setOrder(annotation.order());
        setTemplateName(sourceClass.getName());
        setRunAlways(annotation.runAlways());
        setTransactional(annotation.transactional());
        setNewChangeUnit(true);
        setSystem(false);
    }

}
