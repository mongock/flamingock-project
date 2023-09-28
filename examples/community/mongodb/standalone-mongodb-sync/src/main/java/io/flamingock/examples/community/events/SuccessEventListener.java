package io.flamingock.examples.community.events;


import io.flamingock.core.event.model.SuccessEvent;

import java.util.function.Consumer;

public class SuccessEventListener implements Consumer<SuccessEvent> {

    public static boolean executed = false;

    @Override
    public void accept(SuccessEvent event) {
        executed = true;
    }
}
