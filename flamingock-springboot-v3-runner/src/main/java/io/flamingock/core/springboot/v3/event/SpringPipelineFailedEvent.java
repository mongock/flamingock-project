package io.flamingock.core.springboot.v3.event;

import io.flamingock.core.event.model.IPipelineFailedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineFailedEvent extends ApplicationEvent implements IPipelineFailedEvent {
    private final IPipelineFailedEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineFailedEvent(Object source, IPipelineFailedEvent event) {
        super(source);
        this.event = event;
    }

    @Override
    public Exception getException() {
        return event.getException();
    }



    @Override
    public String toString() {
        return "SpringPipelineFailedEvent{" +
                "event=" + event +
                ", source=" + source +
                '}';
    }
}
