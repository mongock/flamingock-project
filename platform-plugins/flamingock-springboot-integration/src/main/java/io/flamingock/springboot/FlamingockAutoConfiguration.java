/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.springboot;

import io.flamingock.internal.core.builder.FlamingockFactory;
import io.flamingock.internal.core.runner.RunnerBuilder;
import io.flamingock.internal.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.boot.SpringApplication")
@ConditionalOnFlamingockEnabled
@EnableConfigurationProperties(SpringbootProperties.class)
public class FlamingockAutoConfiguration {

    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    public ApplicationRunner applicationRunner(RunnerBuilder runnerBuilder) {
        return SpringbootUtil.toApplicationRunner(runnerBuilder.build());
    }

    @Bean("flamingock-runner")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnExpression("'${flamingock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    public InitializingBean initializingBeanRunner(RunnerBuilder runnerBuilder) {
        return SpringbootUtil.toInitializingBean(runnerBuilder.build());
    }

    @Bean("flamingock-builder")
    @Profile(Constants.NON_CLI_PROFILE)
    @ConditionalOnMissingBean(RunnerBuilder.class)
    public RunnerBuilder flamingockBuilder(SpringbootProperties configurationProperties,
                                           ApplicationContext springContext,
                                           ApplicationEventPublisher applicationEventPublisher) {
        return FlamingockFactory.getEditionAwareBuilder(
                configurationProperties.getCoreConfiguration(),
                configurationProperties.getCloudProperties(),
                configurationProperties.getLocalConfiguration()
        )
                .addDependency(SpringRunnerType.class, configurationProperties.getRunnerType())
                .addDependency(ApplicationContext.class, springContext)
                .addDependency(ApplicationEventPublisher.class, applicationEventPublisher);
    }
}