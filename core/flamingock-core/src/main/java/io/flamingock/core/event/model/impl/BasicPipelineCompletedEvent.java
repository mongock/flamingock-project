package io.flamingock.core.event.model.impl;

import io.flamingock.core.event.model.PipelineCompletedEvent;

public class BasicPipelineCompletedEvent implements PipelineCompletedEvent {

    private final Object result;

    public BasicPipelineCompletedEvent(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
