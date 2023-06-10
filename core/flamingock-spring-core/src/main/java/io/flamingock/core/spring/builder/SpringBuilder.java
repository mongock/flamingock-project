package io.flamingock.core.spring.builder;



import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

public interface SpringBuilder<HOLDER> {
  HOLDER setSpringContext(ApplicationContext springContext);

  HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher);
}
