package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.FailedEvent;

import java.util.function.Consumer;

public class FailureEventListener implements Consumer<FailedEvent> {

    public static boolean executed = false;

    @Override
    public void accept(FailedEvent migrationStartedEvent) {
        executed = true;
    }
}
