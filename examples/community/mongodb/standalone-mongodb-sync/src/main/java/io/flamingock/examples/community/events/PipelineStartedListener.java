package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.IPipelineStartedEvent;

import java.util.function.Consumer;

public class PipelineStartedListener implements Consumer<IPipelineStartedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineStartedEvent migrationStartedEvent) {
        executed = true;
    }
}
