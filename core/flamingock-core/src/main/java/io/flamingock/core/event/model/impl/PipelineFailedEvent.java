package io.flamingock.core.event.model.impl;

import io.flamingock.core.event.model.IPipelineFailedEvent;

public class PipelineFailedEvent implements IPipelineFailedEvent {

    private final Exception throwable;

    public PipelineFailedEvent(Exception throwable) {
        this.throwable = throwable;
    }

    @Override
    public Exception getException() {
        return throwable;
    }
}
