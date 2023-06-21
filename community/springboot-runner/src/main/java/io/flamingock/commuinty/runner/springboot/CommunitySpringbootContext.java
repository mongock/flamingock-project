package io.flamingock.commuinty.runner.springboot;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.core.util.Constants;
import io.flamingock.core.spring.SpringRunnerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@ConditionalOnExpression("${flamingock.enabled:true}")
public class CommunitySpringbootContext {


    @Bean
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    public ApplicationRunner applicationRunner(ConnectionDriver<?> connectionDriver,
                                               CommunitySpringbootConfigurationProperties springConfiguration,
                                               ApplicationContext springContext,
                                               ApplicationEventPublisher applicationEventPublisher) {
        return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
                .buildApplicationRunner();
    }

    @Bean
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    public InitializingBean initializingBeanRunner(ConnectionDriver<?> connectionDriver,
                                                   CommunitySpringbootConfigurationProperties springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
        return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
                .buildInitializingBeanRunner();
    }


    private SpringRunnerBuilder getBuilder(ConnectionDriver<?> connectionDriver,
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
