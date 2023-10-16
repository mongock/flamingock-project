package io.flamingock.core.springboot.v3.event;

import io.flamingock.core.event.model.PipelineFailedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineFailedEvent extends ApplicationEvent implements PipelineFailedEvent {
    private final PipelineFailedEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineFailedEvent(Object source, PipelineFailedEvent event) {
        super(source);
        this.event = event;
    }


    @Override
    public String toString() {
        return "SpringMigrationFailureEvent{" +
                "migrationResult=" + event +
                ", source=" + source +
                "} " + super.toString();
    }

    @Override
    public Exception getException() {
        return event.getException();
    }

    @Override
    public boolean isSuccess() {
        return event.isSuccess();
    }
}
