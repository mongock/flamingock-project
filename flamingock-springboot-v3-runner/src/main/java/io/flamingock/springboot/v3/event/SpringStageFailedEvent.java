package io.flamingock.springboot.v3.event;

import io.flamingock.core.event.model.IPipelineFailedEvent;
import io.flamingock.core.event.model.IStageFailedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringStageFailedEvent extends ApplicationEvent implements IPipelineFailedEvent {
    private final IStageFailedEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringStageFailedEvent(Object source, IStageFailedEvent event) {
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
