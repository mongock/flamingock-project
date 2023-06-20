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


public abstract class MongockContextBase<CONFIG> {

    @Bean
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    public ApplicationRunner applicationRunner(ConnectionDriver<?> connectionDriver,
                                               CONFIG springConfiguration,
                                               ApplicationContext springContext,
                                               ApplicationEventPublisher applicationEventPublisher) {

        return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
                .buildApplicationRunner();
    }

    @Bean
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${mongock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    public InitializingBean initializingBeanRunner(ConnectionDriver<?> connectionDriver,
                                                   CONFIG springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
        return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
                .buildInitializingBeanRunner();
    }

    @SuppressWarnings("all")
    public abstract SpringRunnerBuilder getBuilder(ConnectionDriver<?> connectionDriver,
                                                   CONFIG springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher);
}
