package io.flamingock.core.event.model;

public interface PipelineFailedEvent extends Event {


    Exception getException();


}
