package io.mongock.core.execution.step;

public interface SuccessableStep {

    /**
     * Don't confuse with successful state. It may be a failed step, but its execution was successful.
     * For example, a ManualRolledBackStep. The rollback execution was successful, but it represents a failed step, as the
     * execution failed, and it required rollback.
     *
     * @return if the actual step has been successful
     */
    boolean isSuccessStep();
}
