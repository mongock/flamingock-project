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

package io.flamingock.core.cloud;

import io.flamingock.api.annotations.Flamingock;
import io.flamingock.api.annotations.Stage;
import io.flamingock.api.annotations.SystemStage;

/**
 * Configuration class for Cloud tests that provides @Flamingock annotation
 * with stage configuration matching the pipeline.yaml structure.
 */
@Flamingock(
    systemStage = @SystemStage(sourcesPackage = ""),
    stages = {
        @Stage(
            name = "stage-1",
            description = "First processing stage",
            sourcesPackage = "io.flamingock.core.cloud.changes"
        )
    }
)
public class CloudTestConfiguration {
    // Configuration class - no implementation needed
}