package io.flamingock.core.event.model;

public interface SuccessEvent extends Event {

    default boolean isSuccess() {
        return true;
    }
}
