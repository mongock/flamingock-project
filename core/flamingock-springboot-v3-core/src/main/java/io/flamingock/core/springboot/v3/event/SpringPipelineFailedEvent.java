package io.flamingock.core.springboot.v3.event;

import io.flamingock.core.event.model.FailedEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineFailedEvent extends ApplicationEvent implements FailedEvent {
    private final FailedEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineFailedEvent(Object source, FailedEvent event) {
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
