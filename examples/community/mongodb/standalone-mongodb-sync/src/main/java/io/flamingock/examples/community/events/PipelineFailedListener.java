package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.IPipelineFailedEvent;

import java.util.function.Consumer;

public class PipelineFailedListener implements Consumer<IPipelineFailedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineFailedEvent migrationStartedEvent) {
        executed = true;
    }
}