package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v2.event.SpringPipelineCompletedEvent;
import org.springframework.context.ApplicationListener;

public class PipelineCompletedListener implements ApplicationListener<SpringPipelineCompletedEvent> {
    public boolean executed = false;

    @Override
    public void onApplicationEvent(SpringPipelineCompletedEvent event) {
        executed = true;
    }
}
