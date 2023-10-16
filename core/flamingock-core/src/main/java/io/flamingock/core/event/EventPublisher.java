package io.flamingock.core.event;


import io.flamingock.core.event.model.Event;
import io.flamingock.core.event.model.PipelineCompletedEvent;
import io.flamingock.core.event.model.PipelineFailedEvent;
import io.flamingock.core.event.model.PipelineIgnoredEvent;
import io.flamingock.core.event.model.PipelineStartedEvent;

import java.util.function.Consumer;

public class EventPublisher {

    private Consumer<PipelineStartedEvent> pipelineStartedListener;
    private Consumer<PipelineCompletedEvent> pipelineCompletedListener;


    private Consumer<PipelineIgnoredEvent> pipelineIgnoredListener;
    private Consumer<PipelineFailedEvent> pipelineFailedListener;


    private Consumer<Event> globalEventListener;


    public EventPublisher() {
        this(null, null, null, null);
    }

    public EventPublisher(Consumer<PipelineStartedEvent> pipelineStartedListener,
                          Consumer<PipelineCompletedEvent> pipelineCompletedListener,
                          Consumer<PipelineIgnoredEvent> pipelineIgnoredListener,
                          Consumer<PipelineFailedEvent> pipelineFailedListener) {
        this.pipelineStartedListener = pipelineStartedListener;
        this.pipelineCompletedListener = pipelineCompletedListener;
        this.pipelineIgnoredListener = pipelineIgnoredListener;
        this.pipelineFailedListener = pipelineFailedListener;
    }


    public void publish(Event event) {
        if(event instanceof PipelineStartedEvent && pipelineStartedListener != null) {
            pipelineStartedListener.accept((PipelineStartedEvent) event);

        } else if(event instanceof PipelineCompletedEvent && pipelineCompletedListener != null) {
            pipelineCompletedListener.accept((PipelineCompletedEvent) event);

        } else if(event instanceof PipelineIgnoredEvent && pipelineIgnoredListener != null) {
            pipelineIgnoredListener.accept((PipelineIgnoredEvent) event);

        } else if(event instanceof PipelineFailedEvent && pipelineFailedListener != null) {
            pipelineFailedListener.accept((PipelineFailedEvent) event);

        } else if(globalEventListener != null){
            globalEventListener.accept(event);
        }
    }


}
