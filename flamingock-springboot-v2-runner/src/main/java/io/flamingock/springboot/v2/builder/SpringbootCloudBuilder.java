package io.flamingock.springboot.v2.builder;

import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfigurator;
import io.flamingock.core.configurator.cloud.CloudConfiguratorDelegate;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.springboot.v2.SpringDependencyContext;
import io.flamingock.springboot.v2.SpringRunnerBuilder;
import io.flamingock.springboot.v2.SpringUtil;
import io.flamingock.springboot.v2.configurator.SpringbootConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SpringbootCloudBuilder extends SpringbootBaseBuilder<SpringbootCloudBuilder>
        implements
        CloudConfigurator<SpringbootCloudBuilder>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootCloudBuilder.class);


    private final CloudConfiguratorDelegate<SpringbootCloudBuilder> cloudConfiguratorDelegate;


    SpringbootCloudBuilder(CoreConfiguration coreConfiguration,
                           SpringbootConfiguration springbootConfiguration,
                           CloudConfiguration cloudConfiguration) {
        super(coreConfiguration, springbootConfiguration);
        this.cloudConfiguratorDelegate = new CloudConfiguratorDelegate<>(cloudConfiguration, this::getSelf);
    }

    @Override
    protected SpringbootCloudBuilder getSelf() {
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public Runner build() {

        String[] activeProfiles = SpringUtil.getActiveProfiles(getSpringContext());
        logger.info("Creating runner with spring profiles[{}]", Arrays.toString(activeProfiles));

        return RunnerCreator.create(
                buildPipeline(activeProfiles),
                null,//connectionEngine.getAuditor(),
                null,//connectionEngine.getAuditor(),
                null,//connectionEngine.getTransactionWrapper().orElse(null),
                null,//connectionEngine.getLockProvider(),
                getCoreProperties(),
                createEventPublisher(),
                new SpringDependencyContext(getSpringContext()),
                getCoreProperties().isThrowExceptionIfCannotObtainLock()
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CLOUD
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public SpringbootCloudBuilder setApiKey(String apiKey) {
        return cloudConfiguratorDelegate.setApiKey(apiKey);
    }

    @Override
    public SpringbootCloudBuilder setToken(String token) {
        return cloudConfiguratorDelegate.setToken(token);
    }

    @Override
    public String getApiKey() {
        return cloudConfiguratorDelegate.getApiKey();
    }

    @Override
    public String getToken() {
        return cloudConfiguratorDelegate.getToken();
    }



}
