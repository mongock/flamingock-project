package io.flamingock.examples.community.events;

import io.flamingock.core.springboot.v2.event.SpringPipelineStartedEvent;
import io.flamingock.core.springboot.v2.event.SpringStageStartedEvent;
import org.springframework.context.ApplicationListener;

public class StageStartedListener implements ApplicationListener<SpringStageStartedEvent> {
    public int executed = 0;
    @Override
    public void onApplicationEvent(SpringStageStartedEvent event) {
        executed++;
    }
}
