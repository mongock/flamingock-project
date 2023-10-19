package io.flamingock.examples.community.events;

import io.flamingock.core.event.model.IStageCompletedEvent;
import io.flamingock.core.event.model.IStageStartedEvent;

import java.util.function.Consumer;

public class StageStartedListener implements Consumer<IStageStartedEvent> {

    public static int executed = 0;
    @Override
    public void accept(IStageStartedEvent event) {
        executed++;
    }
}
