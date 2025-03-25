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

package io.flamingock.core.task.preview;

public class CodePreviewLegacyChangeUnit extends CodePreviewChangeUnit {

    private MethodPreview beforeExecutionMethodName;
    private MethodPreview rollbackBeforeExecutionMethodName;

    public CodePreviewLegacyChangeUnit() {
    }

    public CodePreviewLegacyChangeUnit(String id,
                                       String order,
                                       String sourceClassPath,
                                       MethodPreview executionMethodName,
                                       MethodPreview rollbackMethodName,
                                       MethodPreview beforeExecutionMethodName,
                                       MethodPreview rollbackBeforeExecutionMethodName,
                                       boolean runAlways,
                                       boolean transactional,
                                       boolean system) {
        super(id, order, sourceClassPath, executionMethodName, rollbackMethodName, runAlways, transactional, false, system);
        this.beforeExecutionMethodName = beforeExecutionMethodName;
        this.rollbackBeforeExecutionMethodName = rollbackBeforeExecutionMethodName;
    }

    public MethodPreview getBeforeExecutionMethodName() {
        return beforeExecutionMethodName;
    }

    public void setBeforeExecutionMethodName(MethodPreview beforeExecutionMethodName) {
        this.beforeExecutionMethodName = beforeExecutionMethodName;
    }

    public MethodPreview getRollbackBeforeExecutionMethodName() {
        return rollbackBeforeExecutionMethodName;
    }

    public void setRollbackBeforeExecutionMethodName(MethodPreview rollbackBeforeExecutionMethodName) {
        this.rollbackBeforeExecutionMethodName = rollbackBeforeExecutionMethodName;
    }

    @Override
    public String toString() {
        return "CodePreviewLegacyChangeUnit{" + "rollbackBeforeExecutionMethodName='" + rollbackBeforeExecutionMethodName + '\'' +
                ", isNewChangeUnit=" + isNewChangeUnit +
                ", id='" + id + '\'' +
                ", order='" + order + '\'' +
                ", source='" + source + '\'' +
                ", runAlways=" + runAlways +
                ", transactional=" + transactional +
                '}';
    }
}
