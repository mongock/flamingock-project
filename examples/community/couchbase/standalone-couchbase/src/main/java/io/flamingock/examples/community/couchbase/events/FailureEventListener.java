package io.flamingock.examples.community.couchbase.events;

import io.flamingock.core.event.model.PipelineFailedEvent;

import java.util.function.Consumer;

public class FailureEventListener implements Consumer<PipelineFailedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(PipelineFailedEvent migrationStartedEvent) {
        executed = true;
    }
}
