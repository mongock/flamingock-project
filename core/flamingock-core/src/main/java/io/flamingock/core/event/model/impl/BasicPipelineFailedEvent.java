package io.flamingock.core.event.model.impl;

import io.flamingock.core.event.model.PipelineFailedEvent;

public class BasicPipelineFailedEvent implements PipelineFailedEvent {

    private final Exception throwable;

    public BasicPipelineFailedEvent(Exception throwable) {
        this.throwable = throwable;
    }

    @Override
    public Exception getException() {
        return throwable;
    }
}
