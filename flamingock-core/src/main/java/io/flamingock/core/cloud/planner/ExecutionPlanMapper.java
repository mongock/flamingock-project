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

package io.flamingock.core.cloud.planner;

import io.flamingock.core.cloud.lock.CloudLockService;
import io.flamingock.core.cloud.transaction.CloudLocalStatus;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.ServiceId;
import io.flamingock.core.engine.audit.writer.AuditStageStatus;
import io.flamingock.core.engine.lock.Lock;
import io.flamingock.core.pipeline.ExecutableStage;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.util.TimeService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.flamingock.core.engine.audit.writer.AuditEntryStatus.EXECUTED;

public final class ExecutionPlanMapper {

    public static  ExecutionPlanRequest toRequest(List<LoadedStage> loadedStages,
                                                  long lockAcquiredForMillis,
                                                  CloudLocalStatus localStatus) {
        List<ExecutionPlanRequest.Stage> requestStages = new ArrayList<>(loadedStages.size());
        for (int i = 0; i < loadedStages.size(); i++) {
            LoadedStage currentStage = loadedStages.get(i);
            List<String> stageTasks = currentStage
                    .getTaskDescriptors()
                    .stream()
                    .map(TaskDescriptor::getId)
                    .collect(Collectors.toList());
            requestStages.add(new ExecutionPlanRequest.Stage(currentStage.getName(), i, stageTasks));
        }

        return new ExecutionPlanRequest(lockAcquiredForMillis, requestStages, localStatus);
    }

    public static List<ExecutableStage> getExecutableStages(ExecutionPlanResponse response,  List<LoadedStage> loadedStages) {
        //Create a set for the filter in the loop
        Set<String> stageNameSet = response.getStages().stream().map(ExecutionPlanResponse.Stage::getName).collect(Collectors.toSet());

        //Create a map to allow indexed access when looping
        Map<String, ExecutionPlanResponse.Stage> responseStagesMap = response.getStages()
                .stream()
                .collect(Collectors.toMap(ExecutionPlanResponse.Stage::getName, Function.identity()));

        return loadedStages.stream()
                .filter(loadedStage -> stageNameSet.contains(loadedStage.getName()))
                .map(loadedStage -> mapToExecutable(loadedStage, responseStagesMap.get(loadedStage.getName())))
                .collect(Collectors.toList());

    }

    private static ExecutableStage mapToExecutable(LoadedStage loadedStage, ExecutionPlanResponse.Stage stageResponse) {
        Set<String> taskPresentInResponse = new HashSet<>(stageResponse.getTasks());

        AuditStageStatus.StatusBuilder builder = AuditStageStatus.statusBuilder();

        //We assume that if the taskId is not in the response is already successfully executed
        loadedStage
                .getTaskDescriptors()
                .stream()
                .map(TaskDescriptor::getId)
                .filter(id -> !taskPresentInResponse.contains(id))
                .forEach(taskId -> builder.addState(taskId, EXECUTED));

        return loadedStage.applyState(builder.build());
    }

    public static Lock extractLockFromResponse(ExecutionPlanResponse response,
                                               CoreConfigurable coreConfiguration,
                                               RunnerId owner,
                                               CloudLockService lockService,
                                               TimeService timeService) {
        return new Lock(
                owner,
                ServiceId.fromString(response.getLock().getKey()),
                response.getLock().getAcquiredForMillis(),
                coreConfiguration.getLockQuitTryingAfterMillis(),
                coreConfiguration.getLockTryFrequencyMillis(),
                lockService,
                timeService
        );
    }
}