package io.flamingock.examples.community.events;

import io.flamingock.core.event.MigrationSuccessEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<MigrationSuccessEvent> {

    public static boolean executed = false;

    @Override
    public void accept(MigrationSuccessEvent migrationStartedEvent) {
        executed = true;
    }
}
