package io.flamingock.core.event.model.impl;

import io.flamingock.core.event.model.IStageCompletedEvent;

public class StageCompletedEvent implements IStageCompletedEvent {

    private final Object result;

    public StageCompletedEvent(Object result) {
        this.result = result;
    }
    @Override
    public Object getResult() {
        return result;
    }
}
