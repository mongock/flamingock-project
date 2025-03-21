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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.flamingock.core.task.AbstractTaskDescriptor;
import io.flamingock.core.task.loaded.change.CodeLoadedChangeUnit;


/**
 * Adds getters for serialization.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CodedPreviewChangeUnit.class, name = "codedChangeUnit"),
        @JsonSubTypes.Type(value = TemplatePreviewChangeUnit.class, name = "templatedChangeUnit")
})
public abstract class ReflectionPreviewTask extends AbstractTaskDescriptor {


    public ReflectionPreviewTask() {
    }

    public ReflectionPreviewTask(String id,
                                 String order,
                                 String sourceClassPath,
                                 boolean runAlways,
                                 boolean transactional) {
        super(id, order, sourceClassPath, runAlways, transactional);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public CodeLoadedChangeUnit load() {
        return null;
    }

}
