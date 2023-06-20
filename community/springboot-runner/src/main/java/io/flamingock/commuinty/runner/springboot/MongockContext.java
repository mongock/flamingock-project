package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.spring.SpringRunnerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext extends MongockContextBase<CommunitySpringbootConfigurationProperties> {


    @Bean
    public SpringRunnerBuilder getBuilder(ConnectionDriver<?> connectionDriver,
                                          CommunitySpringbootConfigurationProperties properties,
                                          ApplicationContext springContext,
                                          ApplicationEventPublisher applicationEventPublisher) {
        return CommunitySpringboot.builder(
                        properties.getCoreProperties(),
                        properties.getCommunityProperties(),
                        properties.getSpringbootProperties()
                ).setDriver(connectionDriver)
                .setSpringContext(springContext)
                .setEventPublisher(applicationEventPublisher);
    }

}
