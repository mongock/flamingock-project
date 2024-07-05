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

import io.flamingock.core.cloud.transaction.OngoingStatus;

import java.util.List;

public class ExecutionPlanRequest {

    private final long acquiredForMills;

    private final ClientSubmission clientSubmission;

    public ExecutionPlanRequest(long acquiredForMills, List<StageRequest> stages) {
        this.acquiredForMills = acquiredForMills;
        this.clientSubmission = new ClientSubmission(stages);
    }

    public long getAcquiredForMills() {
        return acquiredForMills;
    }

    public ClientSubmission getClientSubmission() {
        return clientSubmission;
    }



    public static class ClientSubmission{
        private final List<StageRequest> stages;

        public ClientSubmission(List<StageRequest> stages) {
            this.stages = stages;
        }

        public List<StageRequest> getStages() {
            return stages;
        }
    }
}

