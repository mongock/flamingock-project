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

package io.flamingock.core.pipeline.execution;

import io.flamingock.core.task.navigation.summary.StepSummary;
import io.flamingock.core.task.navigation.summary.StepSummaryLine;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskSummary implements StepSummary, StepSummaryLine {

    private final String taskId;

    private final List<StepSummaryLine> lines = new LinkedList<>();

    public TaskSummary(String taskId) {
        this.taskId = taskId;
    }

    public void addLine(StepSummaryLine summary) {
        lines.add(summary);
    }

    @Override
    public String getId() {
        return taskId;
    }

    @Override
    public List<StepSummaryLine> getLines() {
        return lines;
    }

    @Override
    public String getPretty() {
        return String.format("\nTask: %s\n%s", taskId, StepSummary.super.getPretty());
    }


}
