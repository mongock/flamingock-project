package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.model.IgnoredEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineIgnoredEvent extends ApplicationEvent implements IgnoredEvent {

    private final IgnoredEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineIgnoredEvent(Object source, IgnoredEvent event) {
        super(source);
        this.event = event;
    }

    @Override
    public boolean isSuccess() {
        return event.isSuccess();
    }

    @Override
    public String toString() {
        return "SpringPipelineIgnoredEvent{" +
                "event=" + event +
                ", source=" + source +
                '}';
    }
}
