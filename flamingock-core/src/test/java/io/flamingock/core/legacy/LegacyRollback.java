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

package io.flamingock.core.legacy;

import io.flamingock.core.legacy_old.navigator.beforeExecution_1.TaskWithBeforeExecution;
import io.flamingock.core.legacy_old.utils.TaskExecutionChecker;
import io.flamingock.core.legacy_old.utils.TestTaskExecution;
import io.flamingock.core.pipeline.execution.ExecutionContext;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class LegacyRollback {

    private static final ExecutionContext EXECUTION_CONTEXT = new ExecutionContext(
            "executionId", "host", "author", new HashMap<>()
    );


    private static final TaskExecutionChecker CHECKER = new TaskExecutionChecker();

    @BeforeEach
    void beforeEach() {
        TaskWithBeforeExecution.checker.reset();
    }

    @Test
    @DisplayName("should run rollback")
    void shouldRunRollback() {
        LegacyTestRunner.runTest(
                ChangeUnitWithExecutionError.class,
                2,
                CHECKER,
                TestTaskExecution.BEFORE_EXECUTION,
                TestTaskExecution.EXECUTION,
                TestTaskExecution.ROLLBACK_EXECUTION);
    }


    @Test
    @DisplayName("should also run before rollback")
    void shouldRunBeforeRollback() {
        LegacyTestRunner.runTest(
                ChangeUnitWithBeforeRollbackExecution.class,
                2,
                CHECKER,
                TestTaskExecution.BEFORE_EXECUTION,
                TestTaskExecution.EXECUTION,
                TestTaskExecution.ROLLBACK_BEFORE_EXECUTION,
                TestTaskExecution.ROLLBACK_EXECUTION);
    }

    @ChangeUnit(id = "taskId", order = "1")
    public static class ChangeUnitWithExecutionError {

        @BeforeExecution
        public void beforeExecution() {
            CHECKER.markBeforeExecution();
        }

        @Execution
        public void execution() {
            CHECKER.markExecution();
            throw new RuntimeException();
        }

        @RollbackExecution
        public void rollbackExecution() {
            CHECKER.markRollBackExecution();
        }
    }

    @ChangeUnit(id = "taskId", order = "1")
    public static class ChangeUnitWithBeforeRollbackExecution {

        @BeforeExecution
        public void beforeExecution() {
            CHECKER.markBeforeExecution();
        }

        @Execution
        public void execution() {
            CHECKER.markExecution();
            throw new RuntimeException();
        }

        @RollbackBeforeExecution
        public void rollbackBeforeExecution() {
            CHECKER.markBeforeExecutionRollBack();
        }

        @RollbackExecution
        public void rollbackExecution() {
            CHECKER.markRollBackExecution();
        }
    }

}