package io.flamingock.examples.community.mongodb.sync.events;

import io.flamingock.core.core.event.MigrationFailureEvent;

import java.util.function.Consumer;

public class FailureEventListener implements Consumer<MigrationFailureEvent> {

    public static boolean executed = false;

    @Override
    public void accept(MigrationFailureEvent migrationStartedEvent) {
        executed = true;
    }
}
