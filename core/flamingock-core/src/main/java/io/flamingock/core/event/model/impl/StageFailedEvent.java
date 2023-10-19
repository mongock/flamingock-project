package io.flamingock.core.event.model.impl;


import io.flamingock.core.event.model.IStageFailedEvent;

public class StageFailedEvent implements IStageFailedEvent {

    private final Exception throwable;

    public StageFailedEvent(Exception throwable) {
        this.throwable = throwable;
    }

    @Override
    public Exception getException() {
        return throwable;
    }
}
