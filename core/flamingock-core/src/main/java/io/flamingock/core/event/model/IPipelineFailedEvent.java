package io.flamingock.core.event.model;

public interface IPipelineFailedEvent extends Event {


    Exception getException();


}
