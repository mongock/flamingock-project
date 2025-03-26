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

package io.flamingock.importer.cloud.common;

import io.flamingock.core.pipeline.PreviewStage;
import io.flamingock.core.system.CloudSystemModule;
import io.flamingock.core.task.preview.CodePreviewChangeUnit;
import io.flamingock.core.task.preview.MethodPreview;
import io.flamingock.core.task.preview.builder.PreviewTaskBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public interface CloudImporter extends CloudSystemModule {
    List<CodePreviewChangeUnit> TASK_CLASSES = Collections.singletonList(
            PreviewTaskBuilder.getCodeBuilder()
                    .setId("importer-v1")
                    .setOrder("1")
                    .setSourceClassPath(ImporterChangeUnit.class.getName())
                    .setExecutionMethod(new MethodPreview("execution", Arrays.asList(ImporterConfiguration.class.getName(), AuditReader.class.getName())))
                    .setRunAlways(false)
                    .setTransactional(true)
                    .setNewChangeUnit(true)
                    .setSystem(true)
                    .build()
    );

    String DEFAULT_MONGOCK_REPOSITORY_NAME = "mongockChangeLog";

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default PreviewStage getStage() {
        return PreviewStage.builder()
                .setName("cloud-importer")
                .setDescription("Cloud importer")
                .setChangeUnitClasses(TASK_CLASSES)
                .build();
    }


    @Override
    default boolean isBeforeUserStages() {
        return true;
    }
}
