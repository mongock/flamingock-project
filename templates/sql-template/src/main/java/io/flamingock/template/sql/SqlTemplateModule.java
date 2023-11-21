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

package io.flamingock.template.sql;


import io.flamingock.template.TemplateModule;
import io.flamingock.template.TemplateSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SqlTemplateModule implements TemplateModule {

    private static final Set<TemplateSpec> templates;

    static {
        HashSet<TemplateSpec> templatesSet = new HashSet<>();
        templatesSet.add(new TemplateSpec("sql-template", SqlTemplate.class));
        templates = Collections.unmodifiableSet(templatesSet);
    }

    @Override
    public Set<TemplateSpec> getTemplates() {
        return templates;
    }

}