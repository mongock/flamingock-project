package io.flamingock.core.spring.springboot;



import io.flamingock.core.core.event.MigrationFailureEvent;
import io.flamingock.core.core.event.MigrationStartedEvent;
import io.flamingock.core.core.event.MigrationSuccessEvent;
import io.flamingock.core.core.runtime.dependency.Dependency;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Consumer;

public interface SpringbootBuilder<HOLDER> {
  HOLDER setSpringContext(ApplicationContext springContext);

  HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher);
}
