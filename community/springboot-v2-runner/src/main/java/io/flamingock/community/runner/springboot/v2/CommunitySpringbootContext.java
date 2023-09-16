package io.flamingock.community.runner.springboot.v2;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.core.springboot.v2.SpringRunnerBuilder;
import io.flamingock.core.util.Constants;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@ConditionalOnExpression("${flamingock.enabled:true}")
public class CommunitySpringbootContext {


    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    public ApplicationRunner applicationRunner(CommunitySpringbootBuilder communitySpringbootBuilder) {
        return communitySpringbootBuilder.buildApplicationRunner();
    }


    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    public InitializingBean initializingBeanRunner(CommunitySpringbootBuilder communitySpringbootBuilder) {
        return communitySpringbootBuilder.buildInitializingBeanRunner();
    }

    @Bean("flamingock-builder")
    @Profile(Constants.NON_CLI_PROFILE)
    public CommunitySpringbootBuilder communitySpringbootBuilder(ConnectionDriver<?> connectionDriver,
                                                   CommunitySpringbootConfiguration configuration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
        return CommunitySpringboot.builder(
                        configuration.getCoreProperties(),
                        configuration.getCommunityProperties(),
                        configuration.getSpringbootProperties()
                ).setDriver(connectionDriver)
                .setSpringContext(springContext)
                .setEventPublisher(applicationEventPublisher);
    }

}
