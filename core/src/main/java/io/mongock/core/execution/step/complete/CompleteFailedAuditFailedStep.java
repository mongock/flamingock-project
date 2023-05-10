package io.mongock.core.execution.step.complete;

import io.mongock.core.execution.step.rolledback.RolledBackStep;
import io.mongock.core.util.FailedWithError;

public final class CompleteFailedAuditFailedStep extends CompleteFailedStep implements FailedWithError {

    private final Throwable error;


    CompleteFailedAuditFailedStep(RolledBackStep taskStep, Throwable error) {
        super(taskStep.getTask(), false);
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }


}
