package io.utils;

import flamingock.core.api.annotations.BeforeExecution;
import flamingock.core.api.annotations.Execution;
import flamingock.core.api.annotations.RollbackBeforeExecution;
import flamingock.core.api.annotations.RollbackExecution;

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
