package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.event.model.PipelineStartedEvent;

import java.util.function.Consumer;

public class StartedEventListener implements Consumer<PipelineStartedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(PipelineStartedEvent migrationStartedEvent) {
        executed = true;
    }
}
