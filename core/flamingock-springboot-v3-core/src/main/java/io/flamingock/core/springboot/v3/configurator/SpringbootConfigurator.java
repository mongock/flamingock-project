package io.flamingock.core.springboot.v3.configurator;



import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

public interface SpringbootConfigurator<HOLDER> {
  HOLDER setSpringContext(ApplicationContext springContext);

  ApplicationContext getSpringContext();

  HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher);

  ApplicationEventPublisher getEventPublisher();

  HOLDER setRunnerType(SpringRunnerType runnerType);

  SpringRunnerType getRunnerType();
}