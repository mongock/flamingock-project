package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.model.PipelineIgnoredEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineIgnoredEvent extends ApplicationEvent implements PipelineIgnoredEvent {

    private final PipelineIgnoredEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineIgnoredEvent(Object source, PipelineIgnoredEvent event) {
        super(source);
        this.event = event;
    }


    @Override
    public String toString() {
        return "SpringPipelineIgnoredEvent{" +
                "event=" + event +
                ", source=" + source +
                '}';
    }
}
