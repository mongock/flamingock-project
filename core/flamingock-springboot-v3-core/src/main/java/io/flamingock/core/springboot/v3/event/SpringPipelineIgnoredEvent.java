package io.flamingock.core.springboot.v3.event;


import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import org.springframework.context.ApplicationEvent;

public class SpringPipelineIgnoredEvent extends ApplicationEvent implements IPipelineIgnoredEvent {

    private final IPipelineIgnoredEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringPipelineIgnoredEvent(Object source, IPipelineIgnoredEvent event) {
        super(source);
        this.event = event;
    }


    @Override
    public String toString() {
        return "SpringMigrationSuccessEvent{" +
                "migrationResult=" + event +
                "} " + super.toString();
    }

}
