/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.internal.common.cloud.planner.request;

import java.util.List;

public class StageRequest {
    private final String name;

    private final int order;

    private final List<TaskRequest> tasks;

    public StageRequest(String name, int order, List<TaskRequest> tasks) {
        this.name = name;
        this.order = order;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public List<TaskRequest> getTasks() {
        return tasks;
    }


}
