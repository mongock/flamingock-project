package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.event.model.IPipelineFailedEvent;

import java.util.function.Consumer;

public class FailureEventListener implements Consumer<IPipelineFailedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(IPipelineFailedEvent migrationStartedEvent) {
        executed = true;
    }
}
