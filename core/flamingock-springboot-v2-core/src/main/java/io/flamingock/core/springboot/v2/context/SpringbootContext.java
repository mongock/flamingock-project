package io.flamingock.core.springboot.v2.context;

import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.springboot.v2.SpringRunnerBuilder;
import io.flamingock.core.springboot.v2.builder.FlamingockSpringboot;
import io.flamingock.core.springboot.v2.builder.SpringbootBaseBuilder;
import io.flamingock.core.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@ConditionalOnExpression("${flamingock.enabled:true}")
public class SpringbootContext {


    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    public ApplicationRunner applicationRunner(SpringRunnerBuilder springbootLocalBuilder) {
        return springbootLocalBuilder.buildApplicationRunner();
    }


    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    public InitializingBean initializingBeanRunner(SpringRunnerBuilder springbootLocalBuilder) {
        return springbootLocalBuilder.buildInitializingBeanRunner();
    }

    @Bean("flamingock-builder")
    @Profile(Constants.NON_CLI_PROFILE)
    public SpringRunnerBuilder localSpringbootBuilder(Optional<ConnectionDriver<?>> connectionDriverOptional,
                                                      FlamingockConfigurationProperties configurationProperties,
                                                      ApplicationContext springContext,
                                                      ApplicationEventPublisher applicationEventPublisher) {
        SpringbootBaseBuilder<?> springRunnerBuilder;
        if (connectionDriverOptional.isPresent() && configurationProperties.isCloudConfigurationEmpty()) {
            springRunnerBuilder = FlamingockSpringboot.localBuilder(
                    configurationProperties.getCoreProperties(),
                    configurationProperties.getSpringbootProperties(),
                    configurationProperties.getLocalProperties()
            ).setDriver(connectionDriverOptional.get());
        } else {
            springRunnerBuilder = FlamingockSpringboot.cloudBuilder(
                    configurationProperties.getCoreProperties(),
                    configurationProperties.getSpringbootProperties(),
                    configurationProperties.getCloudProperties()
            );
        }
        return springRunnerBuilder
                .setSpringContext(springContext)
                .setEventPublisher(applicationEventPublisher);

    }


}
