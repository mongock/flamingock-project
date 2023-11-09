package io.flamingock.examples.community.events;

import io.flamingock.springboot.v2.event.SpringPipelineStartedEvent;
import org.springframework.context.ApplicationListener;

public class PipelineStartedListener implements ApplicationListener<SpringPipelineStartedEvent> {
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineStartedEvent event) {
        executed = true;
    }
}
