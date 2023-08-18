package io.utils;

public class TaskExecutionChecker {

    private boolean executed;

    private boolean rolledBack;
    private boolean beforeExecuted;
    private boolean beforeRolledBack;

    public TaskExecutionChecker() {
        this(false, false, false, false);
    }

    public TaskExecutionChecker(boolean executed, boolean rolledBack, boolean beforeExecuted, boolean beforeRolledBack) {
        this.executed = executed;
        this.rolledBack = rolledBack;
        this.beforeExecuted = beforeExecuted;
        this.beforeRolledBack = beforeRolledBack;
    }

    public void reset() {
        executed = false;
        rolledBack = false;
        beforeExecuted = false;
        beforeRolledBack = false;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void markExecution() {
        executed = true;
    }

    public boolean isRolledBack() {
        return rolledBack;
    }

    public void markRollBackExecution() {
        rolledBack = true;
    }

    public boolean isBeforeExecuted() {
        return beforeExecuted;
    }

    public void markBeforeExecution() {
        beforeExecuted = true;
    }

    public boolean isBeforeExecutionRolledBack() {
        return beforeRolledBack;
    }

    public void markBeforeExecutionRollBack() {
        beforeRolledBack = true;
    }
}
