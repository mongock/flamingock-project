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

import java.beans.Transient;

public class AbstractCodePreviewTask extends AbstractPreviewTask {

    private MethodPreview executionMethodName;
    private MethodPreview rollbackMethodName;

    private String sourcePackage;

    public AbstractCodePreviewTask() {
        super();
    }

    public AbstractCodePreviewTask(String id,
                                   String order,
                                   String sourceClassPath,
                                   MethodPreview executionMethodName,
                                   MethodPreview rollbackMethodName,
                                   boolean runAlways,
                                   boolean transactional,
                                   boolean system) {
        super(id, order, sourceClassPath, runAlways, transactional, system);
        this.executionMethodName = executionMethodName;
        this.rollbackMethodName = rollbackMethodName;
        this.sourcePackage = sourceClassPath.substring(0, sourceClassPath.lastIndexOf("."));
    }

    public MethodPreview getExecutionMethodName() {
        return executionMethodName;
    }

    public void setExecutionMethodName(MethodPreview executionMethodName) {
        this.executionMethodName = executionMethodName;
    }

    public MethodPreview getRollbackMethodName() {
        return rollbackMethodName;
    }

    public void setRollbackMethodName(MethodPreview rollbackMethodName) {
        this.rollbackMethodName = rollbackMethodName;
    }


    @Override
    public String pretty() {
        String fromParent = super.pretty();
        return fromParent + String.format("\n\t\t[class: %s]", getSource());
    }

    @Override
    public String toString() {
        return "PreviewChangeUnit{, id='" + id + '\'' +
                ", order='" + order + '\'' +
                ", source='" + source + '\'' +
                ", runAlways=" + runAlways +
                ", transactional=" + transactional +
                '}';
    }

    @Transient
    public String getSourcePackage() {
        return sourcePackage;
    }
}
