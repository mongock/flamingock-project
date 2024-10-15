package io.flamingock.core.legacy_old;

import io.flamingock.core.legacy_old.utils.TaskExecutionChecker;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

public class TestExecutionTrackerTask {


    public static final TaskExecutionChecker checker = new TaskExecutionChecker();


    @BeforeExecution
    public void beforeExecution() {
        checker.markBeforeExecution();
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution() {
        checker.markBeforeExecutionRollBack();
    }

    @Execution
    public void execution() {
        checker.markExecution();
    }

    @RollbackExecution
    public void rollbackExecution() {
        checker.markRollBackExecution();
    }
}
