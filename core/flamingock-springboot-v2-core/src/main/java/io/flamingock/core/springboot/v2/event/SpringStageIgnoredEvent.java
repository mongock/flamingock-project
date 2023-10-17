package io.flamingock.core.springboot.v2.event;


import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.core.event.model.IStageIgnoredEvent;
import org.springframework.context.ApplicationEvent;

public class SpringStageIgnoredEvent extends ApplicationEvent implements IPipelineIgnoredEvent {

    private final IStageIgnoredEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringStageIgnoredEvent(Object source, IStageIgnoredEvent event) {
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
