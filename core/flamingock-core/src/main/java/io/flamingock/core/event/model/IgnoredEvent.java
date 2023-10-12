package io.flamingock.core.event.model;

public interface IgnoredEvent extends Event {

    @Override
    default boolean isSuccess() {
        return false;
    }
}
