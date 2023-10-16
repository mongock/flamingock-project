package io.flamingock.core.event;


import io.flamingock.core.event.model.PipelineCompletedEvent;
import io.flamingock.core.event.model.PipelineIgnoredEvent;
import io.flamingock.core.event.model.PipelineStartedEvent;

import java.util.function.Consumer;

public class EventPublisher {

    private final Consumer<PipelineStartedEvent> pipelineStartedListener;
    private final Consumer<PipelineCompletedEvent> pipelineCompletedListener;


    private final Consumer<PipelineIgnoredEvent> pipelineIgnoredListener;
    private final Consumer<Exception> pipelineFailedListener;


    public EventPublisher() {
        this(null, null, null, null);
    }

    public EventPublisher(Consumer<PipelineStartedEvent> pipelineStartedListener,
                          Consumer<PipelineCompletedEvent> pipelineCompletedListener,
                          Consumer<PipelineIgnoredEvent> pipelineIgnoredListener,
                          Consumer<Exception> pipelineFailedListener) {
        this.pipelineStartedListener = pipelineStartedListener;
        this.pipelineCompletedListener = pipelineCompletedListener;
        this.pipelineIgnoredListener = pipelineIgnoredListener;
        this.pipelineFailedListener = pipelineFailedListener;
    }

    public void publishPipelineStarted(PipelineStartedEvent event) {
        if (pipelineStartedListener != null) {
            pipelineStartedListener.accept(event);
        }
    }

    public void publishPipelineSuccessEvent(PipelineCompletedEvent event) {
        if (pipelineCompletedListener != null) {
            pipelineCompletedListener.accept(event);
        }
    }

    public void publishPipelineIgnoredEvent(PipelineIgnoredEvent event) {
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
