package io.flamingock.core.core.execution.navigator.tasks.beforeExecution_1;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.utils.TestExecutionTrackerTask;

@ChangeUnit(id = "task-with-before-execution", order = "1")
public class TaskWithBeforeExecution extends TestExecutionTrackerTask {


    @Execution
    public void execution() {
        super.execution();
        throw new RuntimeException("INTENTIONED EXCEPTION");
    }


}
