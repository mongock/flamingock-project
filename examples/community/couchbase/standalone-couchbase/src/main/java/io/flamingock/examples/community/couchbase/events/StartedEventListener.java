package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.event.model.IPipelineStartedEvent;

import java.util.function.Consumer;

public class StartedEventListener implements Consumer<IPipelineStartedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineStartedEvent migrationStartedEvent) {
        executed = true;
    }
}
