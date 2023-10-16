package io.flamingock.core.event;


import io.flamingock.core.event.model.CompletedEvent;
import io.flamingock.core.event.model.IgnoredEvent;
import io.flamingock.core.event.model.StartedEvent;

import java.util.function.Consumer;

public class EventPublisher {

    private final Consumer<StartedEvent> pipelineStartedListener;
    private final Consumer<CompletedEvent> pipelineCompletedListener;


    private final Consumer<IgnoredEvent> pipelineIgnoredListener;
    private final Consumer<Exception> pipelineFailedListener;


    public EventPublisher() {
        this(null, null, null, null);
    }

    public EventPublisher(Consumer<StartedEvent> pipelineStartedListener,
                          Consumer<CompletedEvent> pipelineCompletedListener,
                          Consumer<IgnoredEvent> pipelineIgnoredListener,
                          Consumer<Exception> pipelineFailedListener) {
        this.pipelineStartedListener = pipelineStartedListener;
        this.pipelineCompletedListener = pipelineCompletedListener;
        this.pipelineIgnoredListener = pipelineIgnoredListener;
        this.pipelineFailedListener = pipelineFailedListener;
    }

    public void publishPipelineStarted(StartedEvent event) {
        if (pipelineStartedListener != null) {
            pipelineStartedListener.accept(event);
        }
    }

    public void publishPipelineSuccessEvent(CompletedEvent event) {
        if (pipelineCompletedListener != null) {
            pipelineCompletedListener.accept(event);
        }
    }

    public void publishPipelineIgnoredEvent(IgnoredEvent event) {
        if (pipelineIgnoredListener != null) {
            pipelineIgnoredListener.accept(event);
        }
    }

    public void publishPipelineFailedEvent(Exception ex) {
        if (pipelineFailedListener != null) {
            pipelineFailedListener.accept(ex);
        }
    }


}
